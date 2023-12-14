// write.js

/**
 * 썸머노트 설정
 * */
$(document).ready(function () {
    $('#summernote').summernote({
        placeholder: "내용을 입력해 주세요.",
        height: 600,
        callbacks: {	//여기 부분이 이미지를 첨부하는 부분
            onImageUpload: function (files) {
                uploadSummernoteImageFile(files[0], this);
            },
            onPaste: function (e) {
                const clipboardData = e.originalEvent.clipboardData;
                if (clipboardData && clipboardData.items && clipboardData.items.length) {
                    const item = clipboardData.items[0];
                    if (item.kind === 'file' && item.type.indexOf('image/') !== -1) {
                        e.preventDefault();
                    }
                }
            }
        }
    });

    /**
     * 썸머노트 이미지 파일 업로드
     */
    function uploadSummernoteImageFile(file, editor) {
        // CSRF 토큰을 가져옴
        const csrfToken = $("meta[name='_csrf']").attr("content");
        const csrfHeader = $("meta[name='_csrf_header']").attr("content");

        const data = new FormData();
        data.append("file", file);

        $.ajax({
            data: data,
            type: "POST",
            url: "/board/uploadSummernoteImageFile",
            contentType: false,
            processData: false,
            beforeSend: function (xhr) {
                // CSRF 토큰을 요청 헤더에 추가
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data) {
                // 항상 업로드된 파일의 URL이 있어야 함
                $(editor).summernote('insertImage', data.url);
            }
        });
    }
});

let notOnLoad = true;
window.onload = function() {
    if (!notOnLoad) {
        alert("게시판이 변경되었습니다.");
    }
    updateUrl();
};

/**
 * 게시판 선택 변경
 * */
let currentBoard = "";
function updateUrl() {
    let selectedBoard = document.getElementById("boardName").value;
    let selectedCategory = document.getElementById("category").value;


    if (selectedBoard) {
        // 초기 로드가 아닌 경우에만 알림을 띄우기
        if (!notOnLoad) {
            // 게시판이 변경되었을 때만 메시지 표시
            if (selectedBoard !== currentBoard) {
                alert("게시판이 변경되었습니다.");
            }
        }

        // 동적으로 두 번째 셀렉트 박스의 옵션을 설정
        let categorySelect = document.getElementById("category");
        categorySelect.innerHTML = ""; // 기존 옵션 제거

        addOption(categorySelect, "", "카테고리를 선택하세요");

        switch (selectedBoard) {
            case "sports":
                addOption(categorySelect, 101, "축구");
                addOption(categorySelect, 102, "농구");
                addOption(categorySelect, 103, "야구");
                break;
            case "reading":
                addOption(categorySelect, 201, "소설");
                addOption(categorySelect, 202, "에세이");
                addOption(categorySelect, 203, "자기개발");
                break;
            case "hobby":
                addOption(categorySelect, 301, "여행");
                addOption(categorySelect, 302, "음식");
                addOption(categorySelect, 303, "게임");

                break;
            default:
                break;
        }

        // 만약 선택된 카테고리가 비어 있지 않고, 선택된 카테고리가 현재 존재하는 옵션 중에 있다면
        if (selectedCategory !== "" && categorySelect.querySelector(`option[value="${selectedCategory}"]`)) {
            // 선택된 카테고리를 유지
            categorySelect.value = selectedCategory;
        } else {
            // 그렇지 않으면 카테고리를 선택하세요 표시
            categorySelect.value = "";
        }

        // 현재 선택된 게시판 갱신
        currentBoard = selectedBoard;


        let newUrl = "/board/write?boardName=" + selectedBoard + "&category=" + categorySelect.value;
        history.pushState({}, null, newUrl);
        // 초기 로드를 완료했음을 표시
        notOnLoad = false;
    }
}

function addOption(select, value, text) {
    const option = document.createElement("option");
    option.value = value;
    option.text = text;
    select.add(option);
}

