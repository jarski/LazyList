package org.vaadin.client;

import com.vaadin.shared.communication.ServerRpc;

public interface LazyListServerRpc extends ServerRpc {

	void moreItems();

}
