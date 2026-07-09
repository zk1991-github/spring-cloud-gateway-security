import { Message } from "element-ui";
const showMessage = Symbol("showMessage");
class DonMessage {
    success(options, single = true) {
        this[showMessage]("success", options, single);
    }
    warning(options, single = true) {
        this[showMessage]("warning", options, single);
    }
    info(options, single = true) {
        this[showMessage]("info", options, single);
    }
    error(options, single = true) {
        this[showMessage]("error", options, single);
    }

    [showMessage](type, options, single) {
        if (single) {
            // 判断是否已存在Message
            if (document.getElementsByClassName("el-message").length === 0) {
                Message[type](options);
            }
        } else {
            Message[type](options);
        }
    }
}
// 使用这个
export { DonMessage };
// var MessageOnce = new DonMessage();