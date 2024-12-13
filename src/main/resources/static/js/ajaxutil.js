function authAjaxPost (jQuery,url,data,callback){
    jQuery.ajax({
        url: url,
        type: "post",
        data: data,
        success: callback,
        headers: {
            'Authorization': layui.data('login_user_info_key').authorization
        }
    });
}

function authAjaxGet (jQuery,url,data,callback){
    jQuery.ajax({
        url: url,
        type: "get",
        data: data,
        success: callback,
        headers: {
            'Authorization': layui.data('login_user_info_key').authorization
        }
    });
}

function authAjaxDelete (jQuery,url,data,callback) {
    jQuery.ajax({
        url: url,
        type: "delete",
        data: data,
        success: callback,
        headers: {
            'Authorization': layui.data('login_user_info_key').authorization
        }
    });
}

function authAjaxPut (jQuery,url,data,callback){
    jQuery.ajax({
        url: url,
        type: "put",
        data: data,
        success: callback,
        headers: {
            'Authorization': layui.data('login_user_info_key').authorization
        }
    });
}