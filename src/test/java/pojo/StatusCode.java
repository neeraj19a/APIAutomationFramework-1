package pojo;

public enum StatusCode {
    CODE_200(200, ""),
    CODE_201(201, "Created"),
    CODE_400(400, "Missing required field: name"),
    CODE_401(401, "Invalid access token"),
    CODE_403(403, "Forbidden"),
    CODE_404(404, "Not Found"),
    CODE_405(405, "Method Not Allowed");


    public final int code;
    public final String msg;

    StatusCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

