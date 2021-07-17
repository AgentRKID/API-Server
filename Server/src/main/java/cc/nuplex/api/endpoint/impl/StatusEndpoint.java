package cc.nuplex.api.endpoint.impl;

import cc.nuplex.api.endpoint.Endpoint;

public class StatusEndpoint extends Endpoint {

    public StatusEndpoint() {
        super("status");

        addRoute("", Type.GET, (request, response) -> SUCCESS);
    }

}
