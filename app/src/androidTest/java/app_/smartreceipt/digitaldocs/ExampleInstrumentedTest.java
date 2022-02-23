package app_.smartreceipt.digitaldocs;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 * The androidTest directory should contain the tests that run on real or virtual devices.
 * Such tests include integration tests,
 *
 * end-to-end tests, and other tests where the JVM alone cannot validate your app's functionality.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.digitaldocs", appContext.getPackageName());
    }
}
