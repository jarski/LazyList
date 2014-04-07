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

	static void log(String message) {
		logger.log(Level.INFO, message);
	}

	public static interface MoreItemsHandler {
		public void moreItems();
	}

	private List<MoreItemsHandler> moreItemsHandlers = new ArrayList<MoreItemsHandler>();
	private Widget moreItemsSpinner = createMoreSpinner();
	private boolean fetchingMoreItems = false;

	public LazyListWidget() {
		setStyleName(Theme.LAZYLIST);
		addDomHandler(this, ScrollEvent.getType());
		super.add(moreItemsSpinner);
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

	public void addMoreItemsHandler(MoreItemsHandler handler) {
		moreItemsHandlers.add(handler);
	}

	public boolean contains(Widget widget) {
		return getChildren().contains(widget);
	}

	@Override
	public void onScroll(ScrollEvent event) {
		if (!fetchingMoreItems && moreItemsSpinnerIsVisible()) {
			log("LazyList: fetch more items");
			fetchMoreItems();
		}
	}

	private boolean moreItemsSpinnerIsVisible() {
		int spinnerHeight = Util.getRequiredHeight(moreItemsSpinner);
		return spinnerHeight >= scrollableHeightLeft();
	}

	private int scrollableHeightLeft() {
		int newScrollTop = getElement().getScrollTop();
		int scrollHeight = getElement().getScrollHeight();
		int clientHeight = getElement().getClientHeight();
		return scrollHeight - clientHeight - newScrollTop;
	}

	public void updateChildComponents(List<ComponentConnector> childComponents) {
		remove(moreItemsSpinner);
		for (ComponentConnector child : childComponents) {
			if (!contains(child.getWidget())) {
				add(child.getWidget());
			}
		}
		add(moreItemsSpinner);
		fetchingMoreItems = false;
	}

	private void fetchMoreItems() {
		fetchingMoreItems = true;
		for (MoreItemsHandler handler : moreItemsHandlers) {
			handler.moreItems();
		}
	}

}