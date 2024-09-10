import {Utils} from './utils.js';

mapboxgl.accessToken = 'pk.eyJ1IjoibnVyYmFuaSIsImEiOiJjbHZlMGZkcDkwNDNjMmpudmZvN3g1NGEzIn0.6Ffkwpca9Tm9qn4Hw9n-Tg';

const map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://styles/mapbox/light-v11',
    center: [9.189, 45.464], // starting position [lng, lat]
    zoom: 14, // starting zoom
});

const search = new MapboxGeocoder();
search.accessToken ='pk.eyJ1IjoibnVyYmFuaSIsImEiOiJjbHZlMGZkcDkwNDNjMmpudmZvN3g1NGEzIn0.6Ffkwpca9Tm9qn4Hw9n-Tg';
map.addControl(search);
search.options = {
    language: 'it',
    limit: '5',
};
search.theme = {
    variables: {
        colorPrimary: 'myBrandRed',
        fontFamily: 'Poppins, sans-serif',
        unit: '14px',
        padding: '0.5em',
        borderRadius: '3px',
        boxShadow: '0 0 0 1px silver',
    },
    cssText: ".Input:active { opacity: 0.9; }"
};


map.addControl(new mapboxgl.NavigationControl(), 'top-left');

map.addControl(
    new mapboxgl.GeolocateControl({
        positionOptions: {
            enableHighAccuracy: true
        },
        // When active the map will receive updates to the device's location as it changes.
        trackUserLocation: true,
        // Draw an arrow next to the location dot to indicate which direction the device is heading.
        showUserHeading: true
    })
    , 'top-left');
map.on('load', () => {
    map.loadImage(
        '../images/YUP_event_position.png',
        (error, image) => {
            if (error) throw error;

            map.addImage('yup-event', image);

            map.addSource('party', {
                type: 'geojson',
                data: '/event/api/geo/all'
            });

            map.addLayer({
                'id': 'party',
                'type': 'symbol',
                'source': 'party',
                'layout': {
                    'icon-image': 'yup-event'
                }
            });

            // Create a popup.
            const popup = new mapboxgl.Popup({
                closeButton: false,
                closeOnClick: false
            });

            map.on('mouseenter', 'party', (e) => {

                const popups = document.getElementsByClassName("mapboxgl-popup");

                if (popups.length) {

                    popups[0].remove();

                }

                map.getCanvas().style.cursor = 'pointer';

                let divElement = document.querySelector('div');

                const coordinates = e.features[0].geometry.coordinates.slice();
                const name = Utils.truncateString(e.features[0].properties.name, 25);
                const location = Utils.truncateString(e.features[0].properties.location, 25);
                const tag = e.features[0].properties.tag;
                const eventImage = e.features[0].properties.base64Image;
                const id = e.features[0].properties.id;
                const date = Utils.formatDate(e.features[0].properties.startDateTime);

                while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
                    coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
                }

                if (!eventImage) {
                    if (e.features[0].properties.participants === 0) {
                        new mapboxgl.Popup()
                            .setLngLat(coordinates)
                            .setHTML('<div class="card"><div class="zero-participants-class"><span>FULL</span></div><div class="card-image"> <img src="/images/default-image.jpg" alt="Event Image" class="img-fluid"> </div> <div class="card-body"> <div class="badges"> <div>' + tag + '</div> </div> <h6> <span>' + name + '</span> </h6> <div> <p> <span>' + location + '</span> </p> </div> <div> <p> <span>' + date + '</span> </p> </div> </div> <div class="card-footer"> <div><span><a href="/event/' + id + '">MORE</a></span></div> </div> </div>')
                            .addTo(map);
                    } else if (e.features[0].properties.participants > 0 && e.features[0].properties.participants <= (0.15 * e.features[0].properties.participantsMaxNumber)) {
                        new mapboxgl.Popup()
                            .setLngLat(coordinates)
                            .setHTML('<div class="card"><div class="low-participants-class"><span>FEW LEFT</span></div><div class="card-image"> <img src="/images/default-image.jpg" alt="Event Image" class="img-fluid"> </div> <div class="card-body"> <div class="badges"> <div>' + tag + '</div> </div> <h6> <span>' + name + '</span> </h6> <div> <p> <span>' + location + '</span> </p> </div> <div> <p> <span>' + date + '</span> </p> </div> </div> <div class="card-footer"> <div><span><a href="/event/' + id + '">MORE</a></span></div> </div> </div>')
                            .addTo(map);
                    } else {
                        new mapboxgl.Popup()
                            .setLngLat(coordinates)
                            .setHTML('<div class="card"><div class="high-participants-class"><span>AVAILABLE</span></div><div class="card-image"> <img src="/images/default-image.jpg" alt="Event Image" class="img-fluid"> </div> <div class="card-body"> <div class="badges"> <div>' + tag + '</div> </div> <h6> <span>' + name + '</span> </h6> <div> <p> <span>' + location + '</span> </p> </div> <div> <p> <span>' + date + '</span> </p> </div> </div> <div class="card-footer"> <div><span><a href="/event/' + id + '">MORE</a></span></div> </div> </div>')
                            .addTo(map);
                    }

                } else {
                    if (e.features[0].properties.participants === 0) {
                        new mapboxgl.Popup()
                            .setLngLat(coordinates)
                            .setHTML('<div class="card"><div class="zero-participants-class"><span>FULL</span></div><div class="card-image"> <img src="data:image/jpeg;base64,' + eventImage + '" alt="Event Image" class="img-fluid"> </div> <div class="card-body"> <div class="badges"> <div>' + tag + '</div> </div> <h6> <span>' + name + '</span> </h6> <div> <p> <span>' + location + '</span> </p> </div> <div> <p> <span>' + date + '</span> </p> </div> </div> <div class="card-footer"> <div><span><a href="/event/' + id + '">MORE</a></span></div> </div> </div>')
                            .addTo(map);
                    } else if (e.features[0].properties.participants > 0 && e.features[0].properties.participants <= (0.15 * e.features[0].properties.participantsMaxNumber)) {
                        new mapboxgl.Popup()
                            .setLngLat(coordinates)
                            .setHTML('<div class="card"><div class="low-participants-class"><span>FEW LEFT</span></div><div class="card-image"> <img src="data:image/jpeg;base64,' + eventImage + '" alt="Event Image" class="img-fluid"> </div> <div class="card-body"> <div class="badges"> <div>' + tag + '</div> </div> <h6> <span>' + name + '</span> </h6> <div> <p> <span>' + location + '</span> </p> </div> <div> <p> <span>' + date + '</span> </p> </div> </div> <div class="card-footer"> <div><span><a href="/event/' + id + '">MORE</a></span></div> </div> </div>')
                            .addTo(map);
                    } else {
                        new mapboxgl.Popup()
                            .setLngLat(coordinates)
                            .setHTML('<div class="card"><div class="high-participants-class"><span>AVAILABLE</span></div><div class="card-image"> <img src="data:image/jpeg;base64,' + eventImage + '" alt="Event Image" class="img-fluid"> </div> <div class="card-body"> <div class="badges"> <div>' + tag + '</div> </div> <h6> <span>' + name + '</span> </h6> <div> <p> <span>' + location + '</span> </p> </div> <div> <p> <span>' + date + '</span> </p> </div> </div> <div class="card-footer"> <div><span><a href="/event/' + id + '">MORE</a></span></div> </div> </div>')
                            .addTo(map);
                    }
                }
            });

            // Change it back to a pointer when it leaves.
            map.on('mouseleave', 'party', () => {
                map.getCanvas().style.cursor = '';
                popup.remove();
            });

            // On clicking the icon open event page
            map.on('click', 'party', (e) => {
                let id = e.features[0].properties.id;
                window.location.href = '/event/' + id;
            });
        });
});

document.querySelector('.btn-list-view').addEventListener('click', listView);

//Choice between map and list view
function listView() {
    let map = document.getElementById("map");
    let eventList = document.getElementById("eventList");
    let button = document.getElementById("viewButton");

    let icon = document.getElementById("viewIconContainer");
    let text = document.getElementById("viewText");

    if (map.classList.contains('hidden')) {
        map.classList.remove('hidden');
        eventList.classList.add('hidden');
        icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-card-text" viewBox="0 0 16 16">\n' +
            '  <path d="M14.5 3a.5.5 0 0 1 .5.5v9a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5zm-13-1A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h13a1.5 1.5 0 0 0 1.5-1.5v-9A1.5 1.5 0 0 0 14.5 2z"/>\n' +
            '  <path d="M3 5.5a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9a.5.5 0 0 1-.5-.5M3 8a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9A.5.5 0 0 1 3 8m0 2.5a.5.5 0 0 1 .5-.5h6a.5.5 0 0 1 0 1h-6a.5.5 0 0 1-.5-.5"/>\n' +
            '</svg>';
        window.dispatchEvent(new Event('resize'));
        icon.innerHTML = '<svg id="viewIcon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-card-list" viewBox="0 0 16 16"><path d="M14.5 3a.5.5 0 0 1 .5.5v9a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5zm-13-1A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h13a1.5 1.5 0 0 0 1.5-1.5v-9A1.5 1.5 0 0 0 14.5 2z"/><path d="M5 8a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7A.5.5 0 0 1 5 8m0-2.5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m0 5a.5.5 0 0 1 .5-.5h7a.5.5 0 0 1 0 1h-7a.5.5 0 0 1-.5-.5m-1-5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0M4 8a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0m0 2.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0"/></svg>';
        text.innerText = 'List View';
    } else {
        map.classList.add('hidden');
        eventList.classList.remove('hidden');
        icon.innerHTML = '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-globe-americas" viewBox="0 0 16 16">\n' +
            '  <path d="M8 0a8 8 0 1 0 0 16A8 8 0 0 0 8 0M2.04 4.326c.325 1.329 2.532 2.54 3.717 3.19.48.263.793.434.743.484q-.121.12-.242.234c-.416.396-.787.749-.758 1.266.035.634.618.824 1.214 1.017.577.188 1.168.38 1.286.983.082.417-.075.988-.22 1.52-.215.782-.406 1.48.22 1.48 1.5-.5 3.798-3.186 4-5 .138-1.243-2-2-3.5-2.5-.478-.16-.755.081-.99.284-.172.15-.322.279-.51.216-.445-.148-2.5-2-1.5-2.5.78-.39.952-.171 1.227.182.078.099.163.208.273.318.609.304.662-.132.723-.633.039-.322.081-.671.277-.867.434-.434 1.265-.791 2.028-1.12.712-.306 1.365-.587 1.579-.88A7 7 0 1 1 2.04 4.327Z"/>\n' +
            '</svg>';
    }
}



