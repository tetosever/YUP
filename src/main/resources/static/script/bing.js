let suggestedRoutes = [];

function loadMapScenario() {
    Microsoft.Maps.loadModule('Microsoft.Maps.AutoSuggest', {
        callback: onLoad,
        errorCallback: onError
    });

    function onLoad() {
        const options = {
            maxResults: 5
        };
        let manager = new Microsoft.Maps.AutosuggestManager(options);
        manager.attachAutosuggest('#searchBox', '#searchBoxContainer', selectedSuggestion);
    }

    function onError(message) {
        console.log(message.message)
    }

    function selectedSuggestion(suggestionResult) {
        const suggestedRoute = suggestionResult.formattedSuggestion;
        suggestedRoutes.push(suggestedRoute);
        document.getElementById('searchBox').value = suggestedRoute;
        document.getElementById('latitude').value = suggestionResult.location.latitude;
        document.getElementById('longitude').value = suggestionResult.location.longitude;
    }
}