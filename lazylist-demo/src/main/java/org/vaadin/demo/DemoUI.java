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
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@Theme("demo")
@Title("LazyList Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

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

	@Override
	protected void init(VaadinRequest request) {
		LazyItemFetcher itemFetcher = new LazyItemFetcher() {
			@Override
			public List<Component> getMoreItems() {
				List<Component> personViews = new LinkedList<Component>();
				for (Person person : service.getMorePersons()) {
					personViews.add(createPersonView(person));
				}
				return personViews;
			}
		};
		final LazyList lazylist = new LazyList(itemFetcher);
		lazylist.setSizeFull();
		setContent(lazylist);
	}

	protected Component createPersonView(Person person) {
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
