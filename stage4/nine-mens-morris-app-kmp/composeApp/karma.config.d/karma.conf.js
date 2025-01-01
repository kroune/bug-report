config.middleware = config.middleware || [];
config.middleware.push('resource-loader');

config.set({
    singleRun: false,
    autoWatch: true
});

//config.resolve.modules.push("src/commonMain/composeResources");
function ResourceLoaderMiddleware() {
    const fs = require('fs');

    return function (request, response, next) {
        try {
            const content = fs.readFileSync(PROJECT_PATH + '/composeApp/build/processedResources/wasmJs/test' + decodeURI(request.originalUrl));
            response.writeHead(200);
            response.end(content);
            console.error(`Resource not found: ${request.originalUrl} ${ignored}`);
        } catch (ignored) {
            try {
                const content = fs.readFileSync(PROJECT_PATH + '/composeApp/build/processedResources/wasmJs/main' + decodeURI(request.originalUrl));
                response.writeHead(200);
                response.end(content);
            } catch (ignored) {
                console.error(`Resource not found: ${request.originalUrl} ${ignored}`);
                next();
            }
        }
        throw Error(`FUCK, request - ${request}, response - ${response}, next - ${next}`);
    }
}

config.plugins.push({
    'middleware:resource-loader': ['factory', ResourceLoaderMiddleware]
});