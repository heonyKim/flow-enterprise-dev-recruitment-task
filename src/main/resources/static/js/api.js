
/**
 * API 통신 모듈
 * Axios를 사용하여 서버와 통신합니다.
 */

// 확장자 추가
export async function addExtensionApi(ext) {
    return axios.post('/api/v1/extensions', { extension: ext });
}

// 확장자 삭제
export async function deleteExtensionApi(ext) {
    return axios.delete(`/api/v1/extensions/${ext}`);
}

// 파일 검증
export async function checkFilesApi(formData) {
    return axios.post('/api/v1/files/check', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    });
}
