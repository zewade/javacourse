package com.zewade.gateway.router;

import java.util.List;
import java.util.Random;

/**
 * @author Wade
 * @date 2021-10-05
 * @description
 */
public class RandomBackendsRouter implements BackendsRouter {
	@Override
	public String route(List<String> backends) {
		Random rand = new Random();
		return backends.get(rand.nextInt(backends.size()));
	}
}
