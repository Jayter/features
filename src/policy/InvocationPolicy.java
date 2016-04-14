package policy;

import java.lang.reflect.Method;

/**
 * Created by Nicki on 12.04.2016.
 */
public interface InvocationPolicy {
    Object delegateCall(Method method, Object caller, Object... args);
}
