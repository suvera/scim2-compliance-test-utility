var runId = null;
var runTimer = null;
var lastIndex = 0;
var showTestResults;
var success = 0;
var failed = 0;
var notSupported = 0;
var testFinished = false;
var sequence = 1;

var toggle_div = function (div) {
    $('#' + div).toggle();
};

var beginTest = function () {

    if ($("#endPoint").val() == '') {
        window.alert("SCIM Endpoint must not be empty");
        return;
    }

    if (runTimer != null) {
        window.clearTimeout(runTimer);
        runTimer = null;
    }
    runId = null;
    success = 0;
    failed = 0;
    notSupported = 0;
    lastIndex = 0;
    $('#test-results').html('<h6>Test Results <small class="text-muted">' +
        ' ( scroll down to see all tests ) </small></h6>');

    $('#test-results').show();
    $('#test-results-stats').show();

    $('#tests-success').html(success);
    $('#tests-failed').html(failed);
    $('#tests-not-supported').html(notSupported);
    $('#tests-total').html(failed + success + notSupported);

    testFinished = false;

    $("#run-btn-text").html("Running ...");
    $("#run-btn-spin").show();
    $("#run-btn").prop("disabled", true);

    var params = $("#test-form").serialize();

    $.post("/test/run", params, function (json) {
        runId = json.id;

        setTimeout(showTestResults, 5000);

    }).fail(function () {
        window.alert("Error");
    });
};

var showTestResult = function (test) {
    console.log(test);
    if (test.title == '--DONE--') {
        testFinished = true;
        $("#run-btn-text").html("Go");
        $("#run-btn-spin").hide();
        $("#run-btn").prop("disabled", false);
        return;
    }

    var statusCls = 'alert-success';
    var heading = '';
    if (test.notSupported) {
        statusCls = 'alert-info';
        heading = 'Test: ' + test.title +' (' + test.requestMethod +' - ' + test.responseCode + ') - NOT SUPPORTED!'
        notSupported++;
    } else if (!test.success) {
        statusCls = 'alert-danger';
        failed++;
        heading = 'Test: ' + test.title +' (' + test.requestMethod +' - ' + test.responseCode + ') - Failed!'
    } else {
        success++;
        heading = 'Test: ' + test.title +' (' + test.requestMethod +' - ' + test.responseCode + ') - Success!'
    }

    $('#tests-success').html(success);
    $('#tests-failed').html(failed);
    $('#tests-not-supported').html(notSupported);
    $('#tests-total').html(failed + success + notSupported);

    sequence++;
    var btnStart = '<button type="button" class="btn btn-link btn-sm" ';

    var req = test.requestMethod + "\n\n" + test.requestBody;
    var resp = test.responseCode + "\n\n" + JSON.stringify(test.responseHeaders, null, '\t');
    resp += "\n\n" + test.responseBody;
    if (test.exception !== undefined) {
        resp += "\n" + JSON.stringify(test.exception, null, '\t');
    }

    var row = '';
    row += '<div class="alert ' + statusCls + '" role="alert">';

    row += '<h6 class="alert-heading">' + heading +'</h6>';

    row += btnStart + 'onclick="toggle_div(\'request-' + sequence + '\')">View Request</button>';

    row += '<div style="display: none;" id="request-' + sequence + '">' +
        '<pre><code>' + escapeHtml(req) + '</code></pre></div>';

    row += btnStart + 'onclick="toggle_div(\'response-' + sequence + '\')">View Response</button>';

    row += '<div style="display: none;" id="response-' + sequence + '">' +
        '<pre><code>' + escapeHtml(resp) + '</code></pre></div>';

    row += '</div>';

    $('#test-results').append(row);
};

showTestResults = function () {
    $.get("/test/status?runId=" + runId + "&lastIndex=" + lastIndex, function (json) {
        console.log(json);

        lastIndex = json['nextIndex'];

        if (json['data']) {
            for (i = 0; i < json['data'].length; i++) {
                showTestResult(json['data'][i]);
            }
        }

        if (!testFinished) {
            setTimeout(showTestResults, 5000);
        }

    }).fail(function (data) {
        window.alert("Error " + data);
    });
};

var entityMap = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
    '/': '&#x2F;',
    '`': '&#x60;',
    '=': '&#x3D;'
};

function escapeHtml(string) {
    return String(string).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
    });
}

$(function () {

    $('[data-toggle="tooltip"]').tooltip();

})