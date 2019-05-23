package com.area.areaUtil;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.area.areaUtil.*")
@MapperScan("com.area.areaUtil.dao")
public class AreaUtilApplication {

	public static void main(String[] args) {
		SpringApplication.run(AreaUtilApplication.class, args);
	}

}
