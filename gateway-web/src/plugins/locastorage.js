export const setStore = (name, content) => {
    if (!content) return;
    if (typeof content !== "string") {
        content = JSON.stringify(content);
    }
    localStorage.setItem(name, content);
}

export const getStore = name => {
    if (!name) return;
    return localStorage.getItem(name);
}

export const removeStore = name => {
    if (!name) return;
    localStorage.removeItem(name);
}

export const clearStore = () => {
    localStorage.clear();
}
