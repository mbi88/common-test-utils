package tests;


import entity.Request;
import org.testng.annotations.Test;
import testcase.BaseTestCase;

import static io.restassured.RestAssured.get;

public class FrameworkTest extends BaseTestCase {

    @Test
    public void testName() {
        R r = new R(1);
        System.out.println(r.getId());
        System.out.println(r.withIncorrectId());

        R rr = new R();
        rr.getId();
    }

    class R extends Request<R, Integer> {

        public R(int ad) {
            super(ad);
        }

        public R() {

        }

        public void pist() {
            response = get();
        }
    }
}
