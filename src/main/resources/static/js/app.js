
const header = $('#_csrf_header').attr('content');
const token = $('#_csrf').attr('content');

$(document).ajaxSend(function(e,xhr,options) {
   xhr.setRequestHeader(header, token);
});

$(document).ready(function () {

    $('#chatForm').submit(function (e) {
        e.preventDefault();

        const message = $('#message').val();
        $.post('/chat', { message })
            .done(function(data, status, xhr) {
                // Success (2xx response)
                console.log('2xx:', xhr.status, status, data);
                if(!data) {
                    $('#message').val('');
                } else {
                    alert("Session Expired, Refresh Page or Login again.");
                }
            })
            .fail(function(xhr, status, error) {
                // Failure (non-2xx response)
                console.error('Error:', status, error);
                alert("Session Expired, Refresh Page or Login again.");
            });
    });

    const eventSource = new EventSource('/sse-chat');
    eventSource.onmessage = function (event) {
        if (event.data) {
            const data = JSON.parse(event.data);
            const dateString = new Date(data.timeStamp).toString();
            if(data.message.trim()) {
                const message = `<li class="message"><span class="sender">${data.sender}:</span>${data.message}<small class="timestamp">${dateString}</small></li>`;
                $('#chatMessages').append(message + '');
            }
        }
    };

    eventSource.onerror = function (error) {
        console.error('Chyba SSE:', error);
        eventSource.close();
    };


});
