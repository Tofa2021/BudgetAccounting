module org.example.common {
    exports org.example.request;
    exports org.example.response;
    exports org.example.dto;
    exports org.example.enums;
    exports org.example;
    requires static lombok;
    requires jdk.dynalink;
}