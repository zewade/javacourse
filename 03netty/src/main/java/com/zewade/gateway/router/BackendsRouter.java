package com.zewade.gateway.router;

import java.util.List;

/**
 * @author Wade
 * @date 2021-10-05
 * @description
 */
public interface BackendsRouter {
	/**
	 *
	 * @param backends
	 * @return
	 */
	String route(List<String> backends);
}
