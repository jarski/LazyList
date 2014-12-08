package org.vaadin;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.client.LazyListClientRpc;
import org.vaadin.client.LazyListServerRpc;
import org.vaadin.client.LazyListState;

import com.vaadin.event.EventRouter;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

// This is the server-side UI component that provides public API 
// for LazyList
public class LazyList extends com.vaadin.ui.AbstractComponent implements HasComponents {

	public interface LazyItemFetcher {
		public List<Component> getMoreItems();
	}

	private LazyItemFetcher itemFetcher;
	private List<Component> childComponents = new LinkedList<Component>();
	private EventRouter eventRouter = new EventRouter();

	LazyListClientRpc clientRpc = getRpcProxy(LazyListClientRpc.class);

	public LazyList(LazyItemFetcher itemFetcher) {
		this.itemFetcher = itemFetcher;
		registerRpc(new LazyListServerRpc() {
			@Override
			public void moreItems() {
				askMoreItems();
				clientRpc.moreItemsFetched();
			}
		});
		askMoreItems();
		setSizeFull();
	}

	private void askMoreItems() {
		List<Component> newComponents = itemFetcher.getMoreItems();
		for (Component component : newComponents) {
			childComponents.add(component);
			eventRouter.fireEvent(new ComponentAttachEvent(this, component));
			component.setParent(this);
		}
		markAsDirty();
	}

	@Override
	public LazyListState getState() {
		return (LazyListState) super.getState();
	}

	@Override
	public Iterator<Component> iterator() {
		return childComponents.iterator();
	}

}
