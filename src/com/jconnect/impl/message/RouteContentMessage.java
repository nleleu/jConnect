package com.jconnect.impl.message;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jconnect.core.message.AbstractContentMessage;
import com.jconnect.core.model.RouteModel;

public class RouteContentMessage extends AbstractContentMessage {

	private static final String TAG_ROUTE = "route";
	private ArrayList<RouteModel> routes;

	public RouteContentMessage() {
		routes = new ArrayList<RouteModel>();
	}

	public void addRoute(RouteModel r) {
		routes.add(r);
	}

	public void addRoutes(List<RouteModel> peerRoute) {
		routes.addAll(peerRoute);
	}

	public ArrayList<RouteModel> getRoutes() {
		return routes;
	}

	@Override
	protected JsonObject exportFields(JsonObject json) {
		JsonArray array = new JsonArray();
		for (RouteModel r : routes) {
			array.add(r.toJson());
		}
		json.add(TAG_ROUTE, array);
		return json;
	}

	@Override
	protected void importFields(JsonObject json) {
		if (json.has(TAG_ROUTE)) {
			JsonArray array = json.get(TAG_ROUTE).getAsJsonArray();
			for (JsonElement jsonElement : array) {
				addRoute(new RouteModel((JsonObject) jsonElement));
			}
		}

	}

}
