package org.vaadin.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.Util;

public class LazyListWidget extends FlowPanel implements ScrollHandler {

	private static Logger logger = Logger.getLogger(LazyListWidget.class.getName());

	private class Theme {
		static final String LAZYLIST = "ao-lazylist";
		static final String MOREITEMS = "ao-moreitems";
		static final String SPINNER = "ao-spinner";
	}

	private String textForFetchMoreItems = "Loading..";

	private void log(String message) {
		logger.log(Level.INFO, message);
	}

	public static interface MoreItemsHandler {
		public void moreItems();
	}

	private List<MoreItemsHandler> moreItemsHandlers = new ArrayList<MoreItemsHandler>();
	private Widget moreItemsSpinner = createMoreSpinner();
	private boolean fetchingMoreItems = false;
	private int moreItemsSpinnerHeight = 0;

	public LazyListWidget() {
		setStyleName(Theme.LAZYLIST);
		addDomHandler(this, ScrollEvent.getType());
	}

	private Widget createMoreSpinner() {
		Panel panel = new FlowPanel();
		Label text = new Label(textForFetchMoreItems);
		Label spinner = createSpinner();
		panel.add(text);
		panel.add(spinner);
		panel.setStyleName(Theme.MOREITEMS);
		return panel;
	}

	private Label createSpinner() {
		Label spinner = new Label();
		spinner.setStyleName(Theme.SPINNER);
		return spinner;
	}

	private void measureSizes() {
		moreItemsSpinnerHeight = Util.getRequiredHeight(moreItemsSpinner);
		log("lazylist - more items spinner height measured: " + moreItemsSpinnerHeight);
	}

	public void addMoreItemsHandler(MoreItemsHandler handler) {
		moreItemsHandlers.add(handler);
	}

	public boolean contains(Widget widget) {
		return getChildren().contains(widget);
	}

	@Override
	public void onScroll(ScrollEvent event) {
		shouldMoreItemsSpinnerBeAddedToDOM();
		if (!fetchingMoreItems && moreItemsSpinnerIsVisible()) {
			log("LazyList: fetch more items");
			fetchMoreItems();
		}
	}

	public void moreItemsFetched() {
		log("lazylist - more items fetched");
		if (fetchingMoreItems) {
			log("lazylist - remove spinner");
			remove(moreItemsSpinner);
			fetchingMoreItems = false;
		}
	}

	// This is never called if there was no new childs
	public void updateChildComponents(List<ComponentConnector> childComponents) {
		log("lazylist - update items");
		super.remove(moreItemsSpinner);
		for (ComponentConnector child : childComponents) {
			if (!contains(child.getWidget())) {
				add(child.getWidget());
			}
		}
		fetchingMoreItems = false;
	}

	private void shouldMoreItemsSpinnerBeAddedToDOM() {
		if (moreItemsSpinnerHeight == 0) {
			measureSizes();
		}
		if (scrollableHeightLeft() <= 2 * moreItemsSpinnerHeight) {
			add(moreItemsSpinner);
		}
	}

	private boolean moreItemsSpinnerIsVisible() {
		return moreItemsSpinnerHeight >= scrollableHeightLeft();
	}

	private int scrollableHeightLeft() {
		int newScrollTop = getElement().getScrollTop();
		int scrollHeight = getElement().getScrollHeight();
		int clientHeight = getElement().getClientHeight();
		return scrollHeight - clientHeight - newScrollTop;
	}

	private void fetchMoreItems() {
		log("lazylist - fetch more items");
		fetchingMoreItems = true;
		for (MoreItemsHandler handler : moreItemsHandlers) {
			handler.moreItems();
		}
	}
}