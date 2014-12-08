package org.vaadin.client;

import org.vaadin.LazyList;
import org.vaadin.client.LazyListWidget.MoreItemsHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ConnectorHierarchyChangeEvent;
import com.vaadin.client.DirectionalManagedLayout;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.AbstractHasComponentsConnector;
import com.vaadin.client.ui.layout.MayScrollChildren;
import com.vaadin.shared.ui.Connect;

@Connect(LazyList.class)
public class LazyListConnector extends AbstractHasComponentsConnector implements MayScrollChildren, MoreItemsHandler,
		DirectionalManagedLayout {

	LazyListServerRpc rpc = RpcProxy.create(LazyListServerRpc.class, this);

	public LazyListConnector() {
		registerRpc(LazyListClientRpc.class, new LazyListClientRpc() {
			@Override
			public void moreItemsFetched() {
				getWidget().moreItemsFetched();
			}
		});
		getWidget().addMoreItemsHandler(this);
	}

	@Override
	protected Widget createWidget() {
		return GWT.create(LazyListWidget.class);
	}

	@Override
	public LazyListWidget getWidget() {
		return (LazyListWidget) super.getWidget();
	}

	@Override
	public LazyListState getState() {
		return (LazyListState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		super.onStateChanged(stateChangeEvent);
	}

	@Override
	public void updateCaption(ComponentConnector connector) {

	}

	@Override
	public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
		getWidget().updateChildComponents(getChildComponents());

	}

	@Override
	public void moreItems() {
		rpc.moreItems();
	}

	@Override
	public void layoutVertically() {

	}

	@Override
	public void layoutHorizontally() {

	}

}
