package org.example;

import org.example.dto.Request;
import org.example.dto.Response;
import org.example.dto.Status;
import org.example.service.Service;

import java.util.NoSuchElementException;

public class Controller {
    private final Service service = new Service();

    public Response redirect(Request request) {
        String action = request.getAction();
        return new Response(
                Status.OK,
                switch (action) {
                    default -> {
                        switch (action) {
                            case "Print" -> service.print((String) request.getParams().get("Text"));
                            default -> throw new NoSuchElementException();
                        }
                        yield null;
                    }
                });
    }
}
