
import { addExtensionApi, deleteExtensionApi, checkFilesApi } from './api.js';

document.addEventListener('DOMContentLoaded', () => {
    initFixedExtensions();
    initCustomExtensions();
    initFileCheck();
});

/**
 * 고정 확장자 체크박스 초기화
 */
function initFixedExtensions() {
    const checkboxes = document.querySelectorAll('.fixed-ext-check');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', async function() {
            const ext = this.value;
            const isChecked = this.checked;

            try {
                if (isChecked) {
                    await addExtensionApi(ext);
                } else {
                    await deleteExtensionApi(ext);
                }
            } catch (error) {
                handleError(error);
                // 실패 시 상태 원복
                this.checked = !isChecked;
            }
        });
    });
}

/**
 * 커스텀 확장자 관련 초기화
 */
function initCustomExtensions() {
    const btnAdd = document.getElementById('btnAddCustom');
    const input = document.getElementById('customExtInput');
    const customList = document.getElementById('customExtList');

    // 추가 버튼 클릭
    if (btnAdd) {
        btnAdd.addEventListener('click', () => handleAddCustom(input));
    }

    // 엔터키 입력 지원
    if (input) {
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                handleAddCustom(input);
            }
        });
    }

    // 삭제 버튼 (이벤트 위임 사용)
    if (customList) {
        customList.addEventListener('click', async (e) => {
            const btn = e.target.closest('.btn-delete-custom');
            if (!btn) return;

            const ext = btn.dataset.ext;
            try {
                await deleteExtensionApi(ext);
                location.reload();
            } catch (error) {
                handleError(error);
            }
        });
    }
}

/**
 * 파일 검증 관련 초기화
 */
function initFileCheck() {
    const dropZone = document.getElementById('drop-zone');
    const fileInput = document.getElementById('file-input');

    if (!dropZone || !fileInput) return;

    // 드래그 이벤트 처리
    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.classList.add('border-blue-500', 'bg-blue-50');
    });

    dropZone.addEventListener('dragleave', (e) => {
        e.preventDefault();
        dropZone.classList.remove('border-blue-500', 'bg-blue-50');
    });

    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropZone.classList.remove('border-blue-500', 'bg-blue-50');
        const files = e.dataTransfer.files;
        if (files.length) {
            fileInput.files = files; // 드롭된 파일을 input에 할당
            handleFiles(files, fileInput);
        }
    });

    // 파일 선택 이벤트 처리
    fileInput.addEventListener('change', () => {
        const files = fileInput.files;
        if (files.length) {
            handleFiles(files, fileInput);
        }
    });
}

/**
 * 파일 처리 및 API 호출 핸들러
 * @param {FileList} files
 * @param {HTMLInputElement} fileInput
 */
async function handleFiles(files, fileInput) {
    const maxUploadSize = parseInt(fileInput.dataset.maxUploadSize, 10);
    const maxUploadSizeMB = maxUploadSize / 1024 / 1024;

    for (const file of files) {
        if (file.size > maxUploadSize) {
            alert(`파일 크기 초과: ${file.name} (${(file.size / 1024 / 1024).toFixed(2)}MB)\n최대 파일 크기는 ${maxUploadSizeMB}MB 입니다.`);
            fileInput.value = ''; // 파일 입력 초기화
            return;
        }
    }

    const formData = new FormData();
    for (const file of files) {
        formData.append('files', file);
    }

    try {
        const response = await checkFilesApi(formData);
        displayResults(response.data);
    } catch (error) {
        handleError(error);
    }
}

/**
 * 검증 결과 표시
 * @param {object} data - { allowedFiles: string[], blockedFiles: string[] }
 */
function displayResults(data) {
    const resultZone = document.getElementById('result-zone');
    const allowedList = document.getElementById('allowed-files');
    const blockedList = document.getElementById('blocked-files');

    if (!resultZone || !allowedList || !blockedList) return;

    // 목록 초기화
    allowedList.innerHTML = '';
    blockedList.innerHTML = '';

    // 허용된 파일 목록 채우기
    if (data.allowedFiles && data.allowedFiles.length > 0) {
        data.allowedFiles.forEach(file => {
            const li = document.createElement('li');
            li.textContent = file;
            allowedList.appendChild(li);
        });
    } else {
        const li = document.createElement('li');
        li.textContent = '없음';
        li.className = 'text-gray-500';
        allowedList.appendChild(li);
    }

    // 차단된 파일 목록 채우기
    if (data.blockedFiles && data.blockedFiles.length > 0) {
        data.blockedFiles.forEach(file => {
            const li = document.createElement('li');
            li.textContent = file;
            blockedList.appendChild(li);
        });
    } else {
        const li = document.createElement('li');
        li.textContent = '없음';
        li.className = 'text-gray-500';
        blockedList.appendChild(li);
    }

    resultZone.classList.remove('hidden');
}


/**
 * 커스텀 확장자 추가 핸들러
 */
async function handleAddCustom(inputElement) {
    const ext = inputElement.value.trim();
    if (!ext) {
        alert('확장자를 입력해주세요.');
        return;
    }

    try {
        await addExtensionApi(ext);
        location.reload();
    } catch (error) {
        handleError(error);
    }
}

/**
 * 에러 처리 공통 함수
 */
function handleError(error) {
    const msg = error.response && error.response.data ? error.response.data.message : error.message;
    alert('작업 실패: ' + (msg || '알 수 없는 오류'));
}
