// detail.js


// 댓글 수정 함수
function editComment(button) {
    // 수정할 댓글의 ID를 가져옴
    const commentId = button.getAttribute('data-id');
    // 수정할 댓글의 텍스트 내용을 가져옴
    const commentElement = document.getElementById('comment-' + commentId);
    const commentContent = commentElement.textContent;

    // 텍스트 내용을 수정 가능한 textarea로 대체
    const textarea = document.createElement('textarea');
    textarea.textContent = commentContent;  // 수정된 부분
    commentElement.innerHTML = '';
    commentElement.appendChild(textarea);

    // 수정 가능한 textarea에 포커스를 줌
    textarea.focus();

    // textarea에서 포커스를 잃을 때(수정이 완료되면) 이벤트 처리
    textarea.addEventListener('blur', function () {
        // 수정된 내용을 가져옴
        const newContent = textarea.value.trim();

        // 수정된 내용이 기존 내용과 다르면 서버로 전송 후 해당 댓글 영역에 바로 표시
        if (newContent !== commentContent.trim()) {
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            $.ajax({
                url: '/board/' + id + '/comment/' + commentId + '/update',
                type: 'POST',
                data: {content: newContent},
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    // 수정된 내용으로 댓글 영역 업데이트
                    commentElement.textContent = newContent;
                }
            });
        }
    });
}

//URL 파라미터값 없애기
$( document ).ready(function() {
    history.replaceState({}, null, location.pathname);
});


function likeOrUnlike() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // AJAX를 사용하여 로그인 상태를 확인
    $.ajax({
        url: '/like/checkLogin',
        type: 'GET',
        success: function (loggedIn) {
            if (loggedIn) {
                // 로그인되어 있으면 좋아요 상태를 확인
                $.ajax({
                    url: '/like/status',
                    type: 'GET',
                    data: {
                        boardId: id,
                        memberId: memberId
                    },
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken);
                    },
                    success: function (status) {
                        if (status === 'liked') {
                            // 이미 좋아요를 눌렀으면 취소
                            removeLike();
                        } else {
                            // 아직 좋아요를 누르지 않았으면 좋아요 추가
                            addLike();
                        }
                    }
                });
            } else {
                // 로그인되어 있지 않으면 로그인 모달 띄우기 또는 로그인 페이지로 이동
                alert('로그인이 필요합니다. 로그인 페이지로 이동합니다.');
                window.location.href = '/member/login';
            }
        }
    });
}

function addLike() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // AJAX를 사용하여 좋아요 추가
    $.ajax({
        url: '/like/add',
        type: 'POST',
        data: {
            boardId: id,
            memberId: memberId
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            // 성공 시 좋아요 수 업데이트
            $('#likeCount').text(data);
        }
    });
}

function removeLike() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // AJAX를 사용하여 좋아요 취소
    $.ajax({
        url: '/like/remove',
        type: 'POST',
        data: {
            boardId: id,
            memberId: memberId
        },
        beforeSend: function (xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function (data) {
            // 성공 시 좋아요 수 업데이트
            $('#likeCount').text(data);
        },
        error: function () {
            // 에러 처리 - 좋아요 취소 실패
            alert('좋아요 취소에 실패했습니다. 로그인 상태를 확인하세요.');
        }
    });
}