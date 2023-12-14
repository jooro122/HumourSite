// list.js
function increaseViews(boardId) {
    // AJAX 요청으로 조회수 증가
    $.ajax({
        type: 'GET',
        url: `/board/${boardId}/views`,
        success: function (response) {
            // 조회수 증가 후 상세 페이지로 이동
            const detailUrl = `/board/${boardId}`;
            window.location.href = detailUrl;
        },
        error: function (error) {
            console.error('Error:', error);
        }
    });
}

// 게시판 + 카테고리가 있을시에만 글작성 보여줌
$(document).ready(function () {
    $("#writeButton").hide();

    // 현재 URL에서 boardName과 category 파라미터 추출
    const urlParams = new URLSearchParams(window.location.search);
    const urlBoardName = urlParams.get('boardName');
    const urlCategory = urlParams.get('category');

    if (urlBoardName && urlCategory) {
        $("#writeButton").show();
    }
});
