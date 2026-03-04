package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

import java.util.Map;

@Getter
public class ParamsRequest extends Request {
    private final Map<String, Object> params;

    public ParamsRequest(RequestAction action, Map<String, Object> params) {
        super(action);
        this.params = params;
    }
}
