module org.example.common {
    exports org.example.dto;
    exports org.example.dto.request;
    exports org.example.dto.response;
    exports org.example.dto.model;
    requires static lombok;
    requires jdk.dynalink;
}