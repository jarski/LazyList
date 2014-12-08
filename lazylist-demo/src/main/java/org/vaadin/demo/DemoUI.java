package org.vaadin.demo;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.vaadin.LazyList;
import org.vaadin.LazyList.LazyItemFetcher;

import backend.Person;
import backend.Service;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("LazyList Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI implements ClickListener {

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DemoUI.class, widgetset = "org.vaadin.demo.DemoWidgetSet")
	public static class Servlet extends VaadinServlet {
		@Override
		protected void servletInitialized() throws ServletException {
			super.servletInitialized();
			getService().addSessionInitListener(new DemoSessionInitListener());
		}
	}

	public static class DemoSessionInitListener implements SessionInitListener {

		@Override
		public void sessionInit(SessionInitEvent event) throws ServiceException {
			event.getSession().addBootstrapListener(new BootstrapListener() {

				@Override
				public void modifyBootstrapPage(BootstrapPageResponse response) {
					Document document = response.getDocument();
					Element element = document.createElement("meta");
					element.attr("name", "viewport");
					element.attr("content", "width=device-width, initial-scale=1");
					Element head = response.getDocument().getElementsByTag("head").get(0);
					head.appendChild(element);
				}

				@Override
				public void modifyBootstrapFragment(BootstrapFragmentResponse response) {

				}
			});
		}
	}

	private Service service = new Service();
	private VerticalLayout layout;
	private Button infinite;
	private Button finite;
	private Button couple;
	private LazyList finiteList;
	private LazyList shortList;
	private LazyList infiniteList;

	@Override
	protected void init(VaadinRequest request) {
		infiniteList = createInfiniteList();
		finiteList = createFiniteList();
		shortList = createShortList();

		HorizontalLayout buttons = new HorizontalLayout();
		infinite = new Button("Infinite", this);
		finite = new Button("Finite", this);
		couple = new Button("Short", this);
		buttons.addComponent(infinite);
		buttons.addComponent(finite);
		buttons.addComponent(couple);
		Panel navigationArea = new Panel();
		navigationArea.setContent(buttons);

		layout = new VerticalLayout();
		layout.addComponent(navigationArea);
		layout.addComponent(infiniteList);
		layout.setSizeFull();
		layout.setExpandRatio(infiniteList, 1);

		setContent(layout);
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Button button = event.getButton();
		if (infinite.equals(button)) {
			replaceCurrentListWith(infiniteList);
		}
		if (finite.equals(button)) {
			replaceCurrentListWith(finiteList);
		}
		if (couple.equals(button)) {
			replaceCurrentListWith(shortList);
		}
	}

	private void replaceCurrentListWith(LazyList newList) {
		Component currentList = layout.getComponent(1);
		layout.replaceComponent(currentList, newList);
		layout.setExpandRatio(newList, 1);
	}

	private LazyList createInfiniteList() {
		LazyItemFetcher itemFetcher = new LazyItemFetcher() {
			@Override
			public List<Component> getMoreItems() {
				return fetchMorePersons();
			}

		};
		final LazyList lazylist = new LazyList(itemFetcher);
		return lazylist;
	}

	private LazyList createFiniteList() {
		LazyItemFetcher itemFetcher = new LazyItemFetcher() {
			@Override
			public List<Component> getMoreItems() {
				return fetchFiniteNumberOfPersons();
			}
		};
		final LazyList lazylist = new LazyList(itemFetcher);
		return lazylist;
	}

	private LazyList createShortList() {
		LazyItemFetcher itemFetcher = new LazyItemFetcher() {
			@Override
			public List<Component> getMoreItems() {
				return fetchCoupleOfPersons();
			}
		};
		final LazyList lazylist = new LazyList(itemFetcher);
		return lazylist;
	}

	private List<Component> fetchMorePersons() {
		List<Component> personViews = new LinkedList<Component>();
		for (Person person : service.getMorePersons()) {
			personViews.add(createPersonView(person));
		}
		return personViews;
	}

	private List<Component> fetchFiniteNumberOfPersons() {
		List<Component> personViews = new LinkedList<Component>();
		for (Person person : service.getFiniteNumberOfPersons()) {
			personViews.add(createPersonView(person));
		}
		return personViews;
	}

	private List<Component> fetchCoupleOfPersons() {
		List<Component> personViews = new LinkedList<Component>();
		for (Person person : service.getCoupleOfPersons()) {
			personViews.add(createPersonView(person));
		}
		return personViews;
	}

	private Component createPersonView(Person person) {
		CssLayout cssLayout = new CssLayout();
		cssLayout.addStyleName("person");

		Label name = new Label(person.getFirstname() + " " + person.getLastname());
		name.setSizeUndefined();
		name.addStyleName("name");
		Label city = new Label(person.getCity());
		city.setSizeUndefined();
		city.addStyleName("city");
		Label phone = new Label(person.getPhoneNumber());
		phone.setSizeUndefined();
		phone.addStyleName("phone");

		cssLayout.addComponent(name);
		cssLayout.addComponent(city);
		cssLayout.addComponent(phone);
		return cssLayout;
	}

}
