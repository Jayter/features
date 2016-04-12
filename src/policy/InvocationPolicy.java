package policy;

import java.lang.reflect.Method;

/**
 * Created by Nicki on 12.04.2016.
 */
public interface InvocationPolicy {
    <T> T delegateCall(Method method, Object caller, Object... args);
}
