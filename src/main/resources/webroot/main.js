/**
 * @author Aaron R Miller<aaron.miller@waweb.io>
 */

function main() {
    console.log("Hello, Client App!");

    if (!window.FormData) {
        throw new Error("FormData element is not present.");
    }

    const EB_GATEWAY = location.protocol + "//" + location.host + "/eventbus";
    const EB_HEADERS = { 'X-XSRF-TOKEN': $("meta[name=csrf-token]").attr('content') }
    const EB_OPTIONS = {
        vertxbus_reconnect_attempts_max: Infinity, // Max reconnect attempts
        vertxbus_reconnect_delay_min: 1000, // Initial delay (in ms) before first reconnect attempt
        vertxbus_reconnect_delay_max: 5000, // Max delay (in ms) between reconnect attempts
        vertxbus_reconnect_exponent: 2, // Exponential backoff factor
        vertxbus_randomization_factor: 0.5 // Randomization factor between 0 and 1
    };


    let sending = false;

    function submit(address, payload) {
        console.log("Sending payload to: " + address, payload);
        sending = true;

        const eventBus = new EventBus(EB_GATEWAY, EB_OPTIONS);
        eventBus.onopen = () => {
            console.log("EventBus Opened.");
            const options = { headers: EB_HEADERS }
            eventBus.send(address, payload, options, (err, msg) => {
                if (err) {
                    console.error("Response:", err);
                } else {
                    console.log("Response:", msg);
                }

                sending = false;
                eventBus.close();
            });
        }

        eventBus.onclose = () => {
            // Flag sending as false in case of unexpected close
            sending = false;
            console.log("EventBus Closed.");
        }
    }

    $(".wa-contact-form").on('submit', (event) => {
        event.preventDefault();
        if (sending) return;

        const form = event.target;
        console.log("Attempting submit...");
        if (form.checkValidity() === false) {
            
            // Extract payload from form entries
            const payload = Array.from(new FormData(form).entries())
                .reduce((memo, pair) => {
                    return { ...memo, [pair[0]]: pair[1] }
                }, {});

            submit($(form).attr('action'), payload);
        }

        $(form).addClass('was-validated');
    })
}

$(main);
