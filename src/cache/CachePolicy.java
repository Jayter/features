package cache;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.util.OATHash;
import policy.InvocationPolicy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Nicki on 12.04.2016.
 */
public class CachePolicy implements InvocationPolicy {
    @Override
    public Object delegateCall(Method method, Object caller, Object... args) {
        if(!toBeCached) {
            try {
                return method.invoke(caller, args);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        if(containsEntry(caller, args)) {
            return getResult(caller, args);
        }
        else {
            try {
                Object returnVal = method.invoke(caller, args);
                putEntry(caller, returnVal, args);
                return returnVal;
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private static HashMap<Integer, Object> storage = new HashMap<>();

    public static void initialize(Config config) {
        toBeCached = config.getBoolean("nhandler.cache");
    }

    //set to config.getBoolean("nhandler.cache”) during initialization!
    private static boolean toBeCached;

    private static void putEntry(Object caller, Object result, Object... args) {
        storage.put(getHashCode(caller, args), result);
    }

    private static boolean containsEntry(Object caller, Object... args) {
        return storage.containsKey(getHashCode(caller, args));
    }

    private Object getResult(Object caller, Object... args) {
        return storage.get(getHashCode(caller, args));
    }

    //calculates and returns the hash value of the given parameters
    private static int getHashCode(Object caller, Object... args) {
        int hash;
        if(args != null && args.length != 0) {
            int[] hashCodes = new int[args.length + 1];
            hashCodes[0] = caller != null ? caller.hashCode() : 0;
            for (int i = 1; i < hashCodes.length; i++) {
                Object arg = args[i - 1];
                hashCodes[i] = arg != null ? arg.hashCode() : 0;
            }
            return OATHash.hash(hashCodes);
        }
        else {
            hash = caller != null ? caller.hashCode() : 0;
            return OATHash.hash(hash);
        }
    }
}
