/**
 * 
 */
// SmartEditor 참조를 위한 배열
	var oEditors = [];
	function smartEditor() {
		nhn.husky.EZCreator.createInIFrame({
			oAppRef : oEditors,
			elPlaceHolder : "content",
			sSkinURI : "/smarteditor/SmartEditor2Skin.html",
			fCreator : "createSEditor2"
		});
	 }