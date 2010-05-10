package com.goodworkalan.diffuse;

import static org.testng.Assert.assertEquals;

/**
 * Catch a {@link DiffuseException} and assert that the error code and message
 * are correct.
 * 
 * @author Alan Gutierrez
 */
public class DiffuseExceptionCatcher {
    /** The expected error code. */
    private String code;
    
    /** The expected error message. */
    private String message;
    
    /** The body of the test. */
    private final Runnable runnable;

    /**
     * Assert that the diffusion exception thrown by the given runnable has the
     * given error code and the given error message.
     * 
     * @param code
     *            The expected error code.
     * @param message
     *            The expected error message.
     * @param runnable
     *            The body of the test.
     */
    public DiffuseExceptionCatcher(Runnable runnable, String code, String message) {
        this.code = code;
        this.runnable = runnable;
        this.message = message;
    }

    /**
     * Run the test.
     */
    public void run() {
        try {
            runnable.run();
        } catch (DiffuseException e) {
            assertEquals(e.getMessageKey(), code);
            assertEquals(e.getMessage(), message);
            throw e;
        }
    }
}
