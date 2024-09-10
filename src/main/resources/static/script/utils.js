const Utils = (() => {
    /**
     * Removes whitespace from both ends of a string and replaces multiple spaces with a single space within the string.
     * @param {string} str - The string to normalize.
     * @returns {string} The normalized string.
     */
    const normalizeWhitespace = (str) => {
        return str.trim().replace(/\s+/g, " ");
    };

    /**
     * Checks if the provided string is a valid email address.
     * @param {string} email - The email address to validate.
     * @returns {boolean} True if the email is valid, false otherwise.
     */
    const isEmail = (email) => {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    };

    /**
     * Checks if the provided string is empty.
     * @param {string} str - The string to check.
     * @returns {boolean} True if the string is empty, false otherwise.
     */
    const isEmpty = (str) => {
        return str.trim().length === 0;
    };

    /**
     * Checks if the password meets the specified criteria (minimum 8 characters, at least one letter and one number).
     * @param {string} password - The password to validate.
     * @returns {boolean} True if the password is valid, false otherwise.
     */
    const isPasswordStrong = (password) => {
        const regex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
        return regex.test(password);
    };

    /**
     * Converts a string to lowercase.
     * @param {string} str - The string to convert.
     * @returns {string} The lowercase string.
     */
    const toLowerCase = (str) => {
        return str.toLowerCase();
    };

    /**
     * Sanitizes input to prevent XSS attacks by removing potentially dangerous script, style, and HTML tags.
     * @param {string} str - The string to sanitize.
     * @returns {string} The sanitized string.
     */
    const sanitizeInput = (str) => {
        return str.replace(/<script.*?>.*?<\/script>/gi, "")
            .replace(/<[\\/\\!]*?[^<>]*?>/gi, "")
            .replace(/<style.*?>.*?<\/style>/gi, "")
            .replace(/<![\s\S]*?--[ \t\n\r]*>/gi, "");
    };

    /**
     * Truncates a string to a specified length and adds an ellipsis if it exceeds the limit.
     * @param {string} str - The string to truncate.
     * @param {number} maxLength - The maximum length of the truncated string.
     * @returns {string} The truncated string.
     */
    function truncateString(str, maxLength) {
        if (str.length > maxLength) {
            return str.substring(0, maxLength) + "...";
        }
        return str;
    }

    /**
     * Formats a date string into a human-readable format.
     * @param {string} dateString - The date string in the format "YYYY, MM, DD, HH, mm".
     * @returns {string} The formatted date string.
     */
    function formatDate(dateString) {
        const dateArray = dateString
            .slice(1, -1)
            .split(",")
            .map(Number)

        const date = new Date(dateArray[0], dateArray[1] - 1, dateArray[2], dateArray[3], dateArray[4]);

        const daysOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
        const dayOfWeek = daysOfWeek[date.getDay()];

        const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
        const month = months[date.getMonth()];

        const day = date.getDate();

        const hours = date.getHours().toString().padStart(2, '0');
        const minutes = date.getMinutes().toString().padStart(2, '0');

        return `${dayOfWeek} - ${day} ${month}, ${hours}:${minutes}`;
    }

    /**
     * Validates the image file type and size.
     * @param {HTMLInputElement} input - The input element of type file.
     * @returns {boolean} True if the image is valid, false otherwise.
     */
    const isValidImage = (input) => {
        const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg'];
        const maxSize = 5* 1024 * 1024; //5 MB times bytes

        const file = input.files[0];

        if (!file) return false;

        if (!allowedTypes.includes(file.type) || file.size > maxSize) {
            input.value = "";
            return false;
        }

        return true;
    };

    return {
        normalizeWhitespace,
        isEmail,
        isEmpty,
        isPasswordStrong,
        toLowerCase,
        sanitizeInput,
        formatDate,
        isValidImage,
        truncateString
    };
})();

export {Utils};