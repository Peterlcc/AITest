package com.peter.utils;

public enum CmdEnv {
	sh("/bin/sh"),bash("/bin/bash");
	
	private String env = null;

	private CmdEnv(String env) {
		this.env=env;
	}
	public String getEnv() {
		return env;
	}
}
