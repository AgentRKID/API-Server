package cc.nuplex.api.endpoint.impl;

import cc.nuplex.api.endpoint.Endpoint;

public class ProfileEndpoint extends Endpoint {

    public ProfileEndpoint() {
        super("profile");

        addRoute(":uuid", Type.GET, (request, response) -> "");
    }

}
