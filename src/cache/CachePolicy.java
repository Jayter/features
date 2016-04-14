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
  private static HashMap<Integer, Object> storage = new HashMap<>();

  private static CachePolicy instance;

  private static boolean toBeCached;

  private CachePolicy() {
  }

  public static CachePolicy getInstance() {
    if(instance == null)
      instance = new CachePolicy();
    return instance;
  }

  public static void initialize(Config config) {
    toBeCached = config.getBoolean("nhandler.cache");
  }

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

  private void putEntry(Object caller, Object result, Object... args) {
    storage.put(getHashCode(caller, args), result);
  }

  private boolean containsEntry(Object caller, Object... args) {
    return storage.containsKey(getHashCode(caller, args));
  }

  private Object getResult(Object caller, Object... args) {
    return storage.get(getHashCode(caller, args));
  }

  //calculates and returns the hash value of the given parameters
  private int getHashCode(Object caller, Object... args) {
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
