export default async function sendHttpRequest(
    url,
    method = 'GET',
    body = null
) {

    const token = localStorage.getItem("token");

    let response = await fetch(url, {
        method: method,
        body: body,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { "Authorization": `Bearer ${token}` } : {})
        }
    })

    let jsonData = ""

    try {
        jsonData = await response.json()
    } catch {
        jsonData = "{}"
    }

    return {
        json: jsonData,
        status: response.status,
        responseHeader: response.headers
    }
}
