document.addEventListener('DOMContentLoaded', function() {
    const mainImage = document.getElementById('mainImage');
    const prevButton = document.querySelector('.prev-button');
    const nextButton = document.querySelector('.next-button');
    const thumbnails = document.querySelectorAll('.thumbnail');

    console.log('review-detail.js loaded.');
    console.log('uploadFileNames from Thymeleaf:', uploadFileNames);
    console.log('reviewId from Thymeleaf:', reviewId);
    console.log('initialLikeStatus from Thymeleaf:', initialLikeStatus);

    if (mainImage && thumbnails.length > 0 && uploadFileNames && uploadFileNames.length > 0) {
        let currentIndex = 0;

        function updateImage(index) {
            if (index < 0) {
                currentIndex = uploadFileNames.length - 1;
            } else if (index >= uploadFileNames.length) {
                currentIndex = 0;
            } else {
                currentIndex = index;
            }

            mainImage.src = `/view/${uploadFileNames[currentIndex].link}`;

            thumbnails.forEach((thumb, i) => {
                if (i === currentIndex) {
                    thumb.classList.add('active-thumbnail');
                } else {
                    thumb.classList.remove('active-thumbnail');
                }
            });
        }

        prevButton.addEventListener('click', () => {
            updateImage(currentIndex - 1);
        });

        nextButton.addEventListener('click', () => {
            updateImage(currentIndex + 1);
        });

        thumbnails.forEach(thumbnail => {
            thumbnail.addEventListener('click', function() {
                const index = parseInt(this.dataset.index);
                updateImage(index);
            });
        });

        updateImage(0);
    }

    // 좋아요 기능
    const likeButton = document.getElementById('likeButton');
    const likeIcon = document.getElementById('likeIcon');
    const likeCountSpan = document.getElementById('likeCount');

    if (likeButton && likeIcon && likeCountSpan && typeof reviewId !== 'undefined') {
        // 초기 좋아요 상태 설정
        likeIcon.textContent = initialLikeStatus ? '❤️' : '🤍';

        likeButton.addEventListener('click', async function() {
            try {
                const response = await fetch(`/review/${reviewId}/like`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const data = await response.json();
                likeIcon.textContent = data.liked ? '❤️' : '🤍';
                likeCountSpan.textContent = data.likeCount;
            } catch (error) {
                console.error('좋아요 토글 실패:', error);
                alert('좋아요 처리 중 오류가 발생했습니다.');
            }
        });
    }

    // 댓글 기능
    const commentListDiv = document.getElementById('commentList');
    const commentContentTextarea = document.getElementById('commentContent');
    const addCommentButton = document.getElementById('addCommentButton');

    if (commentListDiv && commentContentTextarea && addCommentButton && typeof reviewId !== 'undefined') {

        // 댓글 로드 함수
        async function loadComments() {
            try {
                const response = await fetch(`/review/${reviewId}/comments`);
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                const comments = await response.json();
                console.log('Loaded comments:', comments); // 댓글 데이터 로그
                commentListDiv.innerHTML = ''; // 기존 댓글 삭제

                if (comments.length === 0) {
                    commentListDiv.innerHTML = '<p>아직 댓글이 없습니다.</p>';
                    return;
                }

                comments.forEach(comment => {
                    const commentDiv = document.createElement('div');
                    commentDiv.className = 'comment-item';
                    commentDiv.dataset.commentId = comment.id; // 댓글 ID 저장

                    commentDiv.innerHTML = `
                        <p><strong>${comment.member_id}</strong>: <span class="comment-text">${comment.content}</span></p>
                        <p class="comment-meta">${new Date(comment.regDate).toLocaleString()}</p>
                        <div class="comment-actions">
                            <button type="button" class="modify-comment-button">수정</button>
                            <button type="button" class="delete-comment-button">삭제</button>
                        </div>
                    `;
                    commentListDiv.appendChild(commentDiv);
                });

                // 수정/삭제 버튼 이벤트 리스너 추가
                commentListDiv.querySelectorAll('.modify-comment-button').forEach(button => {
                    button.addEventListener('click', function() {
                        const commentDiv = this.closest('.comment-item');
                        const commentId = commentDiv.dataset.commentId;
                        const currentContent = commentDiv.querySelector('.comment-text').textContent;

                        const newContent = prompt('댓글을 수정하세요:', currentContent);
                        if (newContent !== null && newContent.trim() !== '') {
                            modifyComment(commentId, newContent.trim());
                        }
                    });
                });

                commentListDiv.querySelectorAll('.delete-comment-button').forEach(button => {
                    button.addEventListener('click', function() {
                        const commentDiv = this.closest('.comment-item');
                        const commentId = commentDiv.dataset.commentId;
                        if (confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
                            deleteComment(commentId);
                        }
                    });
                });

            } catch (error) {
                console.error('댓글 로드 실패:', error);
                commentListDiv.innerHTML = '<p>댓글을 불러오는 데 실패했습니다.</p>';
            }
        }

        // 댓글 등록 함수
        addCommentButton.addEventListener('click', async function() {
            const content = commentContentTextarea.value.trim();
            if (content === '') {
                alert('댓글 내용을 입력해주세요.');
                return;
            }

            try {
                const response = await fetch(`/review/${reviewId}/comments`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ content: content })
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                commentContentTextarea.value = ''; // 입력 필드 초기화
                loadComments(); // 댓글 목록 갱신
            } catch (error) {
                console.error('댓글 등록 실패:', error);
                alert('댓글 등록 중 오류가 발생했습니다.');
            }
        });

        // 댓글 수정 함수
        async function modifyComment(commentId, newContent) {
            try {
                const response = await fetch(`/review/comments/${commentId}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ content: newContent })
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                loadComments(); // 댓글 목록 갱신
            } catch (error) {
                console.error('댓글 수정 실패:', error);
                alert('댓글 수정 중 오류가 발생했습니다.');
            }
        }

        // 댓글 삭제 함수
        async function deleteComment(commentId) {
            try {
                const response = await fetch(`/review/comments/${commentId}`, {
                    method: 'DELETE'
                });
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                loadComments(); // 댓글 목록 갱신
            } catch (error) {
                console.error('댓글 삭제 실패:', error);
                alert('댓글 삭제 중 오류가 발생했습니다.');
            }
        }

        // 페이지 로드 시 댓글 로드
        loadComments();
    }
});