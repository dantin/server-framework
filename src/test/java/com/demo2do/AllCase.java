package com.demo2do;

import com.demo2do.core.utils.ClassUtilsTest;
import com.demo2do.core.utils.CommonUtilsTest;
import junit.extensions.ActiveTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllCase {

    public static Test suite() {
        TestSuite suite = new ActiveTestSuite();

        suite.addTestSuite(CommonUtilsTest.class);
        suite.addTestSuite(ClassUtilsTest.class);

        return suite;
    }
}
