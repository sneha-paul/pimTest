function lockDirection(element) { element.css({ "cursor": "default", "opacity": "0" }); }
function unlockDirection(element) { element.css({ "cursor": "pointer", "opacity": "1" }); }
function checkDirectional(parent_element, container_width, new_css_left, content_width) {
    if (container_width + Math.abs(new_css_left) >= content_width) { lockDirection(parent_element.siblings(".slideIt-right")); }
    else { unlockDirection(parent_element.siblings(".slideIt-right")); }
    if (new_css_left >= 0) { lockDirection(parent_element.siblings(".slideIt-left")); }
    else { unlockDirection(parent_element.siblings(".slideIt-left")); }
}
function checkVerticalDirection(parent_element, container_height, new_css_top, content_height) {
    if (container_height + Math.abs(new_css_top) >= content_height) { lockDirection(parent_element.siblings(".slideIt-down")); }
    else { unlockDirection(parent_element.siblings(".slideIt-down")); }
    if (new_css_top >= 0) { lockDirection(parent_element.siblings(".slideIt-up")); }
    else { unlockDirection(parent_element.siblings(".slideIt-up")); }
}
function getContentInfo(parent_element) { var size_per_block = getFullWidth(parent_element.children().children(":first-child")); var container_width = parent_element.innerWidth(); var content_width = parent_element.children().children().length * size_per_block; var current_css_left = parseInt(parent_element.children().css("left")) >= 0 ? 0 : Math.floor(parseInt(parent_element.children().css("left")) / size_per_block) * size_per_block; var new_css_left = 0; var total_items_visible = Math.floor(container_width / size_per_block); total_items_visible = parent_element.children().children().length < total_items_visible ? parent_element.children().children().length : total_items_visible; return { "size_per_block": size_per_block, "container_width": container_width, "content_width": content_width, "current_css_left": current_css_left, "new_css_left": new_css_left, "total_items_visible": total_items_visible } }
function getHeightInfo(parent_element) { var size_per_block = getFullHeight(parent_element.children().children(":first-child")); var container_height = parent_element.innerHeight(); var content_height = parent_element.children().children().length * size_per_block; var parentElementTop = parent_element.children().css("top") == "auto" ? 0 : parseInt(parent_element.children().css("top")); var current_css_top = parentElementTop >= 0 ? 0 : Math.floor(parentElementTop / size_per_block) * size_per_block; var new_css_top = 0; var total_items_visible = Math.floor(container_height / size_per_block); total_items_visible = parent_element.children().children().length < total_items_visible ? parent_element.children().children().length : total_items_visible; return { "size_per_block": size_per_block, "container_height": container_height, "content_height": content_height, "current_css_top": current_css_top, "new_css_top": new_css_top, "total_items_visible": total_items_visible } }
function condenseContent(speed, element, vertical) {
    var cleanedElementSet = new Array(); $(element).children().children().each(function () { cleanedElementSet.push($(this).clone(true, true)); $(this).remove(); }); for (var i = 0; i < cleanedElementSet.length; i++) { $(element).children().append(cleanedElementSet[i]); }
    element.children().stop(); if (vertical) {
        content_info = getHeightInfo(element); white_space = parseInt((content_info.container_height % content_info.size_per_block) / 2); if (Math.abs(content_info.current_css_top) + content_info.container_height > content_info.content_height && content_info.container_height <= content_info.content_height) { content_info.current_css_top = -(content_info.content_height - content_info.container_height); content_info.new_css_top = (content_info.current_css_top > 0 ? 0 : content_info.current_css_top - white_space); }
        else { content_info.new_css_top = (content_info.current_css_top > 0 ? 0 : content_info.current_css_top + white_space); }
        var total_children_height = content_info.current_css_top; element.children().children().each(function () {
            if ($(this).css('display') != 'none') {
                if (total_children_height >= 0) {
                    if (total_children_height + getFullHeight($(this)) <= content_info.container_height) {
                        $(this).css("visibility", "visible")
                        total_children_height += getFullHeight($(this));
                    }
                    else { $(this).css("visibility", "hidden"); }
                }
                else { $(this).css("visibility", "hidden"); total_children_height += getFullHeight($(this)); }
            }
        }); checkVerticalDirection(element, content_info.container_height, content_info.new_css_top, content_info.content_height)
        element.children().animate({ "top": content_info.new_css_top }, speed);
    } else {
        content_info = getContentInfo(element); white_space = parseInt((content_info.container_width % content_info.size_per_block) / 2); if (Math.abs(content_info.current_css_left) + content_info.container_width > content_info.content_width && content_info.container_width <= content_info.content_width) { content_info.current_css_left = -(content_info.content_width - content_info.container_width); content_info.new_css_left = (content_info.current_css_left > 0 ? 0 : content_info.current_css_left - white_space); }
        else { content_info.new_css_left = (content_info.current_css_left > 0 ? 0 : content_info.current_css_left + white_space); }
        var total_children_width = content_info.current_css_left; element.children().children().each(function () {
            if ($(this).css('display') != 'none') {
                if (total_children_width >= 0) {
                    if (total_children_width + getFullWidth($(this)) <= content_info.container_width) {
                        $(this).css("visibility", "visible")
                        total_children_width += getFullWidth($(this));
                    }
                    else { $(this).css("visibility", "hidden"); }
                }
                else { $(this).css("visibility", "hidden"); total_children_width += getFullWidth($(this)); }
            }
        }); checkDirectional(element, content_info.container_width, content_info.new_css_left, content_info.content_width)
        element.children().animate({ "left": content_info.new_css_left }, speed);
    }
}
function slideIt_init(element) {
    $((typeof element != 'undefined' ? element : '.slideIt')).each(function () { if ($(this).attr('bns-verticalslideit') == 'true') { condenseContent(0, $(this), true); } else { condenseContent(0, $(this)); } }); $('.slideIt-right').off('click.slide').on("click.slide", function () {
        if ($(this).css("cursor") == "pointer") {
            var parent_element = $(this).siblings(".slideIt"); parent_element.children().stop(); content_info = getContentInfo(parent_element); parent_element.children().children().each(function () { $(this).css("visibility", "visible"); }); if (Math.abs(Math.abs(content_info.current_css_left) - content_info.container_width) > content_info.content_width - content_info.container_width) { content_info.new_css_left = -(content_info.content_width - content_info.container_width); }
            else { content_info.new_css_left = content_info.current_css_left - ((content_info.total_items_visible * content_info.size_per_block) - ((content_info.container_width - (content_info.total_items_visible * content_info.size_per_block)) / 2)); }
            checkDirectional(parent_element, content_info.container_width, content_info.new_css_left, content_info.content_width); parent_element.children().animate({ "left": content_info.new_css_left + "px" }, 200, function () { condenseContent(0, parent_element); });
        }
    }); $('.slideIt-left').off('click.slide').on("click.slide", function () {
        if ($(this).css("cursor") == "pointer") {
            var parent_element = $(this).siblings(".slideIt"); parent_element.children().stop(); content_info = getContentInfo(parent_element); parent_element.children().children().each(function () { $(this).css("visibility", "visible"); }); if (content_info.current_css_left + (content_info.total_items_visible * content_info.size_per_block) > 0) { content_info.new_css_left = 0; }
            else { content_info.new_css_left = content_info.current_css_left + ((content_info.total_items_visible * content_info.size_per_block) + ((content_info.container_width - (content_info.total_items_visible * content_info.size_per_block)) / 2)); }
            checkDirectional(parent_element, content_info.container_width, content_info.new_css_left, content_info.content_width); parent_element.children().animate({ "left": content_info.new_css_left + "px" }, 200, function () { condenseContent(0, parent_element); });
        }
    }); $('.slideIt-down').off('click.slide').on("click.slide", function () {
        if ($(this).css("cursor") == "pointer") {
            var parent_element = $(this).siblings(".slideIt"); parent_element.children().stop(); content_info = getHeightInfo(parent_element); parent_element.children().children().each(function () { $(this).css("visibility", "visible"); }); if (Math.abs(Math.abs(content_info.current_css_top) - content_info.container_height) > content_info.content_height - content_info.container_height) { content_info.new_css_top = -(content_info.content_height - content_info.container_height); }
            else { content_info.new_css_top = content_info.current_css_top - ((content_info.total_items_visible * content_info.size_per_block) - ((content_info.container_height - (content_info.total_items_visible * content_info.size_per_block)) / 2)); }
            checkVerticalDirection(parent_element, content_info.container_height, content_info.new_css_top, content_info.content_height); parent_element.children().animate({ "top": content_info.new_css_top + "px" }, 200, function () { condenseContent(0, parent_element, true); });
        }
    }); $('.slideIt-up').off('click.slide').on("click.slide", function () {
        if ($(this).css("cursor") == "pointer") {
            var parent_element = $(this).siblings(".slideIt"); parent_element.children().stop(); content_info = getHeightInfo(parent_element); parent_element.children().children().each(function () { $(this).css("visibility", "visible"); }); if (content_info.current_css_top + (content_info.total_items_visible * content_info.size_per_block) > 0) { content_info.new_css_top = 0; }
            else { content_info.new_css_top = content_info.current_css_top + ((content_info.total_items_visible * content_info.size_per_block) + ((content_info.container_height - (content_info.total_items_visible * content_info.size_per_block)) / 2)); }
            checkVerticalDirection(parent_element, content_info.container_height, content_info.new_css_top, content_info.content_height); parent_element.children().animate({ "top": content_info.new_css_top + "px" }, 200, function () { condenseContent(0, parent_element, true); });
        }
    });
}
slideIt_init(); $(document).ready(function () { $(window).on("resize", function () { if (!ignoreIE()) { waitForFinalEvent(function () { $(".slideIt").each(function () { if ($(this).attr('bns-verticalslideit') == 'true') { condenseContent(0, $(this), true); } else { condenseContent(0, $(this)); } }); }, 200, "slideIt_Id"); } }); });