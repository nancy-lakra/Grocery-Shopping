package com.training.grocery.user.datamodel;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class StringRandom {
	public String generateRandomString(Long n, String s) {
		String hex = "@" + Long.toHexString(n * n);
		String res = s.concat(hex);
		return res;
	}
}
