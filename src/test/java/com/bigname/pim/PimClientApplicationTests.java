package com.bigname.pim;

import com.bigname.pim.PimApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes={PimApplication.class})
public class PimClientApplicationTests {

	@Test
	public void contextLoads() {
	}

}
