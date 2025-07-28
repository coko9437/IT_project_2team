console.log("JavaScript loaded and executing.");
document.addEventListener('DOMContentLoaded', function() {
    const stars = document.querySelectorAll('#star-rating .star');
    const ratingInput = document.getElementById('rating');
    let currentRating = parseInt(ratingInput.value);

    // 별 시각적 상태 업데이트 함수
    function updateStarVisuals(value) {
        console.log(`updateStarVisuals called with value: ${value}`);
        stars.forEach(star => {
            const starValue = parseInt(star.dataset.value);
            if (starValue <= value) {
                star.classList.add('selected');
                console.log(`Star ${starValue} added 'selected'`);
            } else {
                star.classList.remove('selected');
                console.log(`Star ${starValue} removed 'selected'`);
            }
        });
    }

    stars.forEach(star => {
        star.addEventListener('click', function() {
            const value = parseInt(this.dataset.value);
            ratingInput.value = value;
            currentRating = value; // 현재 선택된 평점 업데이트
            console.log(`Clicked star value: ${value}, currentRating: ${currentRating}`);
            updateStarVisuals(value); // 클릭 시 selected 클래스 적용
        });

        star.addEventListener('mouseover', function() {
            const value = parseInt(this.dataset.value);
            console.log(`Mouseover star value: ${value}`);
            updateStarVisuals(value); // 마우스 오버 시 별 채우기
        });

        star.addEventListener('mouseout', function() {
            console.log(`Mouseout, restoring to currentRating: ${currentRating}`);
            updateStarVisuals(currentRating); // 마우스 아웃 시 현재 선택된 평점으로 복원
        });
    });

    // 초기 로드 시 별 상태 업데이트 (기본값 0이므로 모두 빈 별)
    console.log(`Initial load, currentRating: ${currentRating}`);
    updateStarVisuals(currentRating);

    // 파일 업로드 로직
    const fileInput = document.getElementById('fileInput');
    const uploadResultDiv = document.getElementById('uploadResult');
    const uploadLoadingDiv = document.getElementById('uploadLoading'); // 로딩 div 추가
    const submitButton = document.getElementById('submitButton'); // submit 버튼 추가
    const form = document.querySelector('form');

    console.log(`uploadLoadingDiv found: ${uploadLoadingDiv !== null}`);
    console.log(`submitButton found: ${submitButton !== null}`);

    // 파일 삭제 로직 (함수를 DOMContentLoaded 스코프 밖으로 이동하여 전역 접근 가능하게 함)
    function removeFile(uuid, fileName, elementToRemove) {
        console.log(`Attempting to delete file: ${uuid}_${fileName}`);
        fetch(`/removeFile/${uuid}_${fileName}`, { // 백엔드 삭제 엔드포인트 호출
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                console.log('File deleted successfully from server.');
                elementToRemove.remove();

                const hiddenInputs = form.querySelectorAll(`input[name^="uploadFileNames"][value="${uuid}"]`);
                hiddenInputs.forEach(input => input.remove());
            } else {
                console.error('Failed to delete file from server.');
                alert('파일 삭제에 실패했습니다.');
            }
        })
        .catch(error => {
            console.error('Error during file deletion:', error);
            alert('파일 삭제 중 오류가 발생했습니다.');
        });
    }

    if (fileInput) {
        fileInput.addEventListener('change', function(e) {
            const files = e.target.files;
            if (files.length === 0) {
                return;
            }

            // 현재 업로드된 파일명 목록 가져오기
            const existingFileNames = Array.from(uploadResultDiv.querySelectorAll('.file-item span'))
                                        .map(span => span.textContent);
            console.log('Existing file names:', existingFileNames);

            const filesToUpload = [];
            let hasDuplicate = false;

            for (let i = 0; i < files.length; i++) {
                const newFile = files[i];
                if (existingFileNames.includes(newFile.name)) {
                    alert(`경고: '${newFile.name}' 파일은 이미 존재합니다. 중복 업로드할 수 없습니다.`);
                    hasDuplicate = true;
                } else {
                    filesToUpload.push(newFile);
                }
            }

            if (filesToUpload.length === 0) {
                // 업로드할 파일이 없으면 로딩 표시 및 버튼 상태 변경하지 않음
                return;
            }

            // 업로드 시작 시 로딩 표시 및 버튼 비활성화
            if (uploadLoadingDiv) {
                uploadLoadingDiv.style.display = 'block';
                console.log('Upload loading div displayed.');
            }
            if (submitButton) {
                submitButton.disabled = true;
                console.log('Submit button disabled.');
            }

            const formData = new FormData();
            filesToUpload.forEach(file => {
                formData.append('files', file);
            });

            fetch('/review/upload', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                console.log('Upload successful:', data);

                data.forEach((fileInfo) => {
                    const fileContainer = document.createElement('div');
                    fileContainer.className = 'file-item';
                    fileContainer.dataset.uuid = fileInfo.uuid;

                    const imgPath = `/view/${fileInfo.link}`;
                    const imgElement = document.createElement('img');
                    imgElement.src = imgPath;
                    imgElement.alt = fileInfo.fileName;
                    imgElement.style.maxWidth = '100px';
                    imgElement.style.maxHeight = '100px';
                    imgElement.style.margin = '5px';
                    fileContainer.appendChild(imgElement);

                    const fileNameSpan = document.createElement('span');
                    fileNameSpan.textContent = fileInfo.fileName;
                    fileContainer.appendChild(fileNameSpan);

                    const deleteButton = document.createElement('button');
                    deleteButton.textContent = 'X';
                    deleteButton.className = 'delete-button';
                    deleteButton.type = 'button';
                    deleteButton.dataset.uuid = fileInfo.uuid;
                    deleteButton.dataset.fileName = fileInfo.fileName;
                    deleteButton.addEventListener('click', function() {
                        removeFile(fileInfo.uuid, fileInfo.fileName, fileContainer);
                    });
                    fileContainer.appendChild(deleteButton);

                    uploadResultDiv.appendChild(fileContainer);

                    const newIndex = Math.floor(form.querySelectorAll('input[name^="uploadFileNames"]').length / 3);

                    const uuidInput = document.createElement('input');
                    uuidInput.type = 'hidden';
                    uuidInput.name = `uploadFileNames[${newIndex}].uuid`;
                    uuidInput.value = fileInfo.uuid;
                    form.appendChild(uuidInput);

                    const fileNameInput = document.createElement('input');
                    fileNameInput.type = 'hidden';
                    fileNameInput.name = `uploadFileNames[${newIndex}].fileName`;
                    fileNameInput.value = fileInfo.fileName;
                    form.appendChild(fileNameInput);

                    const imgInput = document.createElement('input');
                    imgInput.type = 'hidden';
                    imgInput.name = `uploadFileNames[${newIndex}].img`;
                    imgInput.value = fileInfo.img;
                    form.appendChild(imgInput);
                });
            })
            .catch(error => {
                console.error('Upload failed:', error);
                alert('파일 업로드에 실패했습니다.');
            })
            .finally(() => {
                // 업로드 완료 시 로딩 숨김 및 버튼 활성화
                if (uploadLoadingDiv) {
                    uploadLoadingDiv.style.display = 'none';
                    console.log('Upload loading div hidden.');
                }
                if (submitButton) {
                    submitButton.disabled = false;
                    console.log('Submit button enabled.');
                }
            });
        });
    }

    // modify.html에서 기존 이미지 삭제 버튼에 이벤트 리스너 추가
    const existingDeleteButtons = uploadResultDiv.querySelectorAll('.delete-button');
    existingDeleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const uuid = this.dataset.uuid;
            const fileName = this.dataset.fileName;
            const fileContainer = this.closest('.file-item');
            removeFile(uuid, fileName, fileContainer);
        });
    });
});
