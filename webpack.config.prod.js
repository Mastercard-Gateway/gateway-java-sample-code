const fs = require('fs');
const path = require('path');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const ProgressPlugin = require('webpack/lib/ProgressPlugin');
const CircularDependencyPlugin = require('circular-dependency-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const autoprefixer = require('autoprefixer');
const postcssUrl = require('postcss-url');
const cssnano = require('cssnano');
const customProperties = require('postcss-custom-properties');
const LinkMediaHTMLPlugin = require('link-media-html-webpack-plugin');

const { NoEmitOnErrorsPlugin, EnvironmentPlugin, HashedModuleIdsPlugin } = require('webpack');
const { BaseHrefWebpackPlugin, SuppressExtractedTextChunksWebpackPlugin } = require('@angular/cli/plugins/webpack');
const { CommonsChunkPlugin, ModuleConcatenationPlugin } = require('webpack').optimize;
const { LicenseWebpackPlugin } = require('license-webpack-plugin');
const { AngularCompilerPlugin } = require('@ngtools/webpack');
const { PurifyPlugin } = require('@angular-devkit/build-optimizer');

const nodeModules = path.join(process.cwd(), 'node_modules');
const realNodeModules = fs.realpathSync(nodeModules);
const genDirNodeModules = path.join(process.cwd(), 'src', '$$_gendir', 'node_modules');
const entryPoints = ["inline", "polyfills", "sw-register", "styles", "vendor", "main"];
const minimizeCss = true;
let baseHref = "";
let deployUrl = "";

// -----------------------------------
//          Stylesheet config
// -----------------------------------
const screenStyleExtractor = new ExtractTextPlugin({
  "filename": "[name].[contenthash:20].bundle.css"
});

const postcssPlugins = function () {
  // safe settings based on: https://github.com/ben-eb/cssnano/issues/358#issuecomment-283696193
  const importantCommentRe = /@preserve|@license|[@#]\s*source(?:Mapping)?URL|^!/i;
  const minimizeOptions = {
    autoprefixer: false,
    safe: true,
    mergeLonghand: false,
    discardComments: { remove: (comment) => !importantCommentRe.test(comment) }
  };

  return [
    postcssUrl({
      url: (URL) => {
        // Only convert root relative URLs, which CSS-Loader won't process into require().
        if (!URL.startsWith('/') || URL.startsWith('//')) {
          return URL;
        }
        if (deployUrl.match(/:\/\//)) {
          // If deployUrl contains a scheme, ignore baseHref use deployUrl as is.
          return `${deployUrl.replace(/\/$/, '')}${URL}`;
        }
        else if (baseHref.match(/:\/\//)) {
          // If baseHref contains a scheme, include it as is.
          return baseHref.replace(/\/$/, '') +
            `/${deployUrl}/${URL}`.replace(/\/\/+/g, '/');
        }
        else {
          // Join together base-href, deploy-url and the original URL.
          // Also dedupe multiple slashes into single ones.
          return `/${baseHref}/${deployUrl}/${URL}`.replace(/\/\/+/g, '/');
        }
      }
    }),
    autoprefixer(),
    customProperties({ preserve: true })
  ].concat(minimizeCss ? [cssnano(minimizeOptions)] : []);
};

// -----------------------------------
//          Main Webpack Config
// -----------------------------------

module.exports = function(env) {

  baseHref = env.baseHref;
  deployUrl = baseHref + env.locale + '/';

  return {
  "resolve": {
    "extensions": [
      ".ts",
      ".js"
    ],
    "modules": [
      "./node_modules",
      "./node_modules"
    ],
    "symlinks": true,
    "alias": {
      "rxjs/AsyncSubject": path.join(process.cwd(), "/node_modules/rxjs/_esm5/AsyncSubject.js"),
      "rxjs/BehaviorSubject": path.join(process.cwd(), "/node_modules/rxjs/_esm5/BehaviorSubject.js"),
      "rxjs/InnerSubscriber": path.join(process.cwd(), "/node_modules/rxjs/_esm5/InnerSubscriber.js"),
      "rxjs/Notification": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Notification.js"),
      "rxjs/Observable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Observable.js"),
      "rxjs/Observer": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Observer.js"),
      "rxjs/Operator": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Operator.js"),
      "rxjs/OuterSubscriber": path.join(process.cwd(), "/node_modules/rxjs/_esm5/OuterSubscriber.js"),
      "rxjs/ReplaySubject": path.join(process.cwd(), "/node_modules/rxjs/_esm5/ReplaySubject.js"),
      "rxjs/Rx": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Rx.js"),
      "rxjs/Scheduler": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Scheduler.js"),
      "rxjs/Subject": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Subject.js"),
      "rxjs/SubjectSubscription": path.join(process.cwd(), "/node_modules/rxjs/_esm5/SubjectSubscription.js"),
      "rxjs/Subscriber": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Subscriber.js"),
      "rxjs/Subscription": path.join(process.cwd(), "/node_modules/rxjs/_esm5/Subscription.js"),
      "rxjs/add/observable/bindCallback": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/bindCallback.js"),
      "rxjs/add/observable/bindNodeCallback": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/bindNodeCallback.js"),
      "rxjs/add/observable/combineLatest": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/combineLatest.js"),
      "rxjs/add/observable/concat": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/concat.js"),
      "rxjs/add/observable/defer": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/defer.js"),
      "rxjs/add/observable/dom/ajax": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/dom/ajax.js"),
      "rxjs/add/observable/dom/webSocket": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/dom/webSocket.js"),
      "rxjs/add/observable/empty": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/empty.js"),
      "rxjs/add/observable/forkJoin": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/forkJoin.js"),
      "rxjs/add/observable/from": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/from.js"),
      "rxjs/add/observable/fromEvent": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/fromEvent.js"),
      "rxjs/add/observable/fromEventPattern": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/fromEventPattern.js"),
      "rxjs/add/observable/fromPromise": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/fromPromise.js"),
      "rxjs/add/observable/generate": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/generate.js"),
      "rxjs/add/observable/if": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/if.js"),
      "rxjs/add/observable/interval": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/interval.js"),
      "rxjs/add/observable/merge": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/merge.js"),
      "rxjs/add/observable/never": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/never.js"),
      "rxjs/add/observable/of": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/of.js"),
      "rxjs/add/observable/onErrorResumeNext": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/onErrorResumeNext.js"),
      "rxjs/add/observable/pairs": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/pairs.js"),
      "rxjs/add/observable/race": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/race.js"),
      "rxjs/add/observable/range": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/range.js"),
      "rxjs/add/observable/throw": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/throw.js"),
      "rxjs/add/observable/timer": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/timer.js"),
      "rxjs/add/observable/using": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/using.js"),
      "rxjs/add/observable/zip": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/observable/zip.js"),
      "rxjs/add/operator/audit": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/audit.js"),
      "rxjs/add/operator/auditTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/auditTime.js"),
      "rxjs/add/operator/buffer": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/buffer.js"),
      "rxjs/add/operator/bufferCount": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/bufferCount.js"),
      "rxjs/add/operator/bufferTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/bufferTime.js"),
      "rxjs/add/operator/bufferToggle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/bufferToggle.js"),
      "rxjs/add/operator/bufferWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/bufferWhen.js"),
      "rxjs/add/operator/catch": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/catch.js"),
      "rxjs/add/operator/combineAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/combineAll.js"),
      "rxjs/add/operator/combineLatest": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/combineLatest.js"),
      "rxjs/add/operator/concat": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/concat.js"),
      "rxjs/add/operator/concatAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/concatAll.js"),
      "rxjs/add/operator/concatMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/concatMap.js"),
      "rxjs/add/operator/concatMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/concatMapTo.js"),
      "rxjs/add/operator/count": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/count.js"),
      "rxjs/add/operator/debounce": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/debounce.js"),
      "rxjs/add/operator/debounceTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/debounceTime.js"),
      "rxjs/add/operator/defaultIfEmpty": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/defaultIfEmpty.js"),
      "rxjs/add/operator/delay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/delay.js"),
      "rxjs/add/operator/delayWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/delayWhen.js"),
      "rxjs/add/operator/dematerialize": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/dematerialize.js"),
      "rxjs/add/operator/distinct": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/distinct.js"),
      "rxjs/add/operator/distinctUntilChanged": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/distinctUntilChanged.js"),
      "rxjs/add/operator/distinctUntilKeyChanged": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/distinctUntilKeyChanged.js"),
      "rxjs/add/operator/do": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/do.js"),
      "rxjs/add/operator/elementAt": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/elementAt.js"),
      "rxjs/add/operator/every": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/every.js"),
      "rxjs/add/operator/exhaust": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/exhaust.js"),
      "rxjs/add/operator/exhaustMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/exhaustMap.js"),
      "rxjs/add/operator/expand": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/expand.js"),
      "rxjs/add/operator/filter": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/filter.js"),
      "rxjs/add/operator/finally": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/finally.js"),
      "rxjs/add/operator/find": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/find.js"),
      "rxjs/add/operator/findIndex": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/findIndex.js"),
      "rxjs/add/operator/first": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/first.js"),
      "rxjs/add/operator/groupBy": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/groupBy.js"),
      "rxjs/add/operator/ignoreElements": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/ignoreElements.js"),
      "rxjs/add/operator/isEmpty": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/isEmpty.js"),
      "rxjs/add/operator/last": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/last.js"),
      "rxjs/add/operator/let": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/let.js"),
      "rxjs/add/operator/map": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/map.js"),
      "rxjs/add/operator/mapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/mapTo.js"),
      "rxjs/add/operator/materialize": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/materialize.js"),
      "rxjs/add/operator/max": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/max.js"),
      "rxjs/add/operator/merge": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/merge.js"),
      "rxjs/add/operator/mergeAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/mergeAll.js"),
      "rxjs/add/operator/mergeMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/mergeMap.js"),
      "rxjs/add/operator/mergeMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/mergeMapTo.js"),
      "rxjs/add/operator/mergeScan": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/mergeScan.js"),
      "rxjs/add/operator/min": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/min.js"),
      "rxjs/add/operator/multicast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/multicast.js"),
      "rxjs/add/operator/observeOn": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/observeOn.js"),
      "rxjs/add/operator/onErrorResumeNext": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/onErrorResumeNext.js"),
      "rxjs/add/operator/pairwise": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/pairwise.js"),
      "rxjs/add/operator/partition": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/partition.js"),
      "rxjs/add/operator/pluck": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/pluck.js"),
      "rxjs/add/operator/publish": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/publish.js"),
      "rxjs/add/operator/publishBehavior": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/publishBehavior.js"),
      "rxjs/add/operator/publishLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/publishLast.js"),
      "rxjs/add/operator/publishReplay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/publishReplay.js"),
      "rxjs/add/operator/race": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/race.js"),
      "rxjs/add/operator/reduce": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/reduce.js"),
      "rxjs/add/operator/repeat": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/repeat.js"),
      "rxjs/add/operator/repeatWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/repeatWhen.js"),
      "rxjs/add/operator/retry": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/retry.js"),
      "rxjs/add/operator/retryWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/retryWhen.js"),
      "rxjs/add/operator/sample": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/sample.js"),
      "rxjs/add/operator/sampleTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/sampleTime.js"),
      "rxjs/add/operator/scan": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/scan.js"),
      "rxjs/add/operator/sequenceEqual": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/sequenceEqual.js"),
      "rxjs/add/operator/share": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/share.js"),
      "rxjs/add/operator/shareReplay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/shareReplay.js"),
      "rxjs/add/operator/single": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/single.js"),
      "rxjs/add/operator/skip": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/skip.js"),
      "rxjs/add/operator/skipLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/skipLast.js"),
      "rxjs/add/operator/skipUntil": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/skipUntil.js"),
      "rxjs/add/operator/skipWhile": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/skipWhile.js"),
      "rxjs/add/operator/startWith": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/startWith.js"),
      "rxjs/add/operator/subscribeOn": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/subscribeOn.js"),
      "rxjs/add/operator/switch": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/switch.js"),
      "rxjs/add/operator/switchMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/switchMap.js"),
      "rxjs/add/operator/switchMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/switchMapTo.js"),
      "rxjs/add/operator/take": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/take.js"),
      "rxjs/add/operator/takeLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/takeLast.js"),
      "rxjs/add/operator/takeUntil": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/takeUntil.js"),
      "rxjs/add/operator/takeWhile": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/takeWhile.js"),
      "rxjs/add/operator/throttle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/throttle.js"),
      "rxjs/add/operator/throttleTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/throttleTime.js"),
      "rxjs/add/operator/timeInterval": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/timeInterval.js"),
      "rxjs/add/operator/timeout": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/timeout.js"),
      "rxjs/add/operator/timeoutWith": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/timeoutWith.js"),
      "rxjs/add/operator/timestamp": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/timestamp.js"),
      "rxjs/add/operator/toArray": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/toArray.js"),
      "rxjs/add/operator/toPromise": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/toPromise.js"),
      "rxjs/add/operator/window": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/window.js"),
      "rxjs/add/operator/windowCount": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/windowCount.js"),
      "rxjs/add/operator/windowTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/windowTime.js"),
      "rxjs/add/operator/windowToggle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/windowToggle.js"),
      "rxjs/add/operator/windowWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/windowWhen.js"),
      "rxjs/add/operator/withLatestFrom": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/withLatestFrom.js"),
      "rxjs/add/operator/zip": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/zip.js"),
      "rxjs/add/operator/zipAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/add/operator/zipAll.js"),
      "rxjs/interfaces": path.join(process.cwd(), "/node_modules/rxjs/_esm5/interfaces.js"),
      "rxjs/observable/ArrayLikeObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/ArrayLikeObservable.js"),
      "rxjs/observable/ArrayObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/ArrayObservable.js"),
      "rxjs/observable/BoundCallbackObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/BoundCallbackObservable.js"),
      "rxjs/observable/BoundNodeCallbackObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/BoundNodeCallbackObservable.js"),
      "rxjs/observable/ConnectableObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/ConnectableObservable.js"),
      "rxjs/observable/DeferObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/DeferObservable.js"),
      "rxjs/observable/EmptyObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/EmptyObservable.js"),
      "rxjs/observable/ErrorObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/ErrorObservable.js"),
      "rxjs/observable/ForkJoinObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/ForkJoinObservable.js"),
      "rxjs/observable/FromEventObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/FromEventObservable.js"),
      "rxjs/observable/FromEventPatternObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/FromEventPatternObservable.js"),
      "rxjs/observable/FromObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/FromObservable.js"),
      "rxjs/observable/GenerateObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/GenerateObservable.js"),
      "rxjs/observable/IfObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/IfObservable.js"),
      "rxjs/observable/IntervalObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/IntervalObservable.js"),
      "rxjs/observable/IteratorObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/IteratorObservable.js"),
      "rxjs/observable/NeverObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/NeverObservable.js"),
      "rxjs/observable/PairsObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/PairsObservable.js"),
      "rxjs/observable/PromiseObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/PromiseObservable.js"),
      "rxjs/observable/RangeObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/RangeObservable.js"),
      "rxjs/observable/ScalarObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/ScalarObservable.js"),
      "rxjs/observable/SubscribeOnObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/SubscribeOnObservable.js"),
      "rxjs/observable/TimerObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/TimerObservable.js"),
      "rxjs/observable/UsingObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/UsingObservable.js"),
      "rxjs/observable/bindCallback": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/bindCallback.js"),
      "rxjs/observable/bindNodeCallback": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/bindNodeCallback.js"),
      "rxjs/observable/combineLatest": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/combineLatest.js"),
      "rxjs/observable/concat": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/concat.js"),
      "rxjs/observable/defer": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/defer.js"),
      "rxjs/observable/dom/AjaxObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/dom/AjaxObservable.js"),
      "rxjs/observable/dom/WebSocketSubject": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/dom/WebSocketSubject.js"),
      "rxjs/observable/dom/ajax": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/dom/ajax.js"),
      "rxjs/observable/dom/webSocket": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/dom/webSocket.js"),
      "rxjs/observable/empty": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/empty.js"),
      "rxjs/observable/forkJoin": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/forkJoin.js"),
      "rxjs/observable/from": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/from.js"),
      "rxjs/observable/fromEvent": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/fromEvent.js"),
      "rxjs/observable/fromEventPattern": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/fromEventPattern.js"),
      "rxjs/observable/fromPromise": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/fromPromise.js"),
      "rxjs/observable/generate": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/generate.js"),
      "rxjs/observable/if": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/if.js"),
      "rxjs/observable/interval": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/interval.js"),
      "rxjs/observable/merge": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/merge.js"),
      "rxjs/observable/never": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/never.js"),
      "rxjs/observable/of": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/of.js"),
      "rxjs/observable/onErrorResumeNext": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/onErrorResumeNext.js"),
      "rxjs/observable/pairs": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/pairs.js"),
      "rxjs/observable/race": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/race.js"),
      "rxjs/observable/range": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/range.js"),
      "rxjs/observable/throw": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/throw.js"),
      "rxjs/observable/timer": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/timer.js"),
      "rxjs/observable/using": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/using.js"),
      "rxjs/observable/zip": path.join(process.cwd(), "/node_modules/rxjs/_esm5/observable/zip.js"),
      "rxjs/operator/audit": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/audit.js"),
      "rxjs/operator/auditTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/auditTime.js"),
      "rxjs/operator/buffer": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/buffer.js"),
      "rxjs/operator/bufferCount": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/bufferCount.js"),
      "rxjs/operator/bufferTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/bufferTime.js"),
      "rxjs/operator/bufferToggle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/bufferToggle.js"),
      "rxjs/operator/bufferWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/bufferWhen.js"),
      "rxjs/operator/catch": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/catch.js"),
      "rxjs/operator/combineAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/combineAll.js"),
      "rxjs/operator/combineLatest": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/combineLatest.js"),
      "rxjs/operator/concat": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/concat.js"),
      "rxjs/operator/concatAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/concatAll.js"),
      "rxjs/operator/concatMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/concatMap.js"),
      "rxjs/operator/concatMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/concatMapTo.js"),
      "rxjs/operator/count": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/count.js"),
      "rxjs/operator/debounce": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/debounce.js"),
      "rxjs/operator/debounceTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/debounceTime.js"),
      "rxjs/operator/defaultIfEmpty": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/defaultIfEmpty.js"),
      "rxjs/operator/delay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/delay.js"),
      "rxjs/operator/delayWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/delayWhen.js"),
      "rxjs/operator/dematerialize": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/dematerialize.js"),
      "rxjs/operator/distinct": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/distinct.js"),
      "rxjs/operator/distinctUntilChanged": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/distinctUntilChanged.js"),
      "rxjs/operator/distinctUntilKeyChanged": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/distinctUntilKeyChanged.js"),
      "rxjs/operator/do": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/do.js"),
      "rxjs/operator/elementAt": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/elementAt.js"),
      "rxjs/operator/every": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/every.js"),
      "rxjs/operator/exhaust": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/exhaust.js"),
      "rxjs/operator/exhaustMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/exhaustMap.js"),
      "rxjs/operator/expand": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/expand.js"),
      "rxjs/operator/filter": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/filter.js"),
      "rxjs/operator/finally": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/finally.js"),
      "rxjs/operator/find": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/find.js"),
      "rxjs/operator/findIndex": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/findIndex.js"),
      "rxjs/operator/first": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/first.js"),
      "rxjs/operator/groupBy": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/groupBy.js"),
      "rxjs/operator/ignoreElements": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/ignoreElements.js"),
      "rxjs/operator/isEmpty": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/isEmpty.js"),
      "rxjs/operator/last": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/last.js"),
      "rxjs/operator/let": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/let.js"),
      "rxjs/operator/map": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/map.js"),
      "rxjs/operator/mapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/mapTo.js"),
      "rxjs/operator/materialize": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/materialize.js"),
      "rxjs/operator/max": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/max.js"),
      "rxjs/operator/merge": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/merge.js"),
      "rxjs/operator/mergeAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/mergeAll.js"),
      "rxjs/operator/mergeMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/mergeMap.js"),
      "rxjs/operator/mergeMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/mergeMapTo.js"),
      "rxjs/operator/mergeScan": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/mergeScan.js"),
      "rxjs/operator/min": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/min.js"),
      "rxjs/operator/multicast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/multicast.js"),
      "rxjs/operator/observeOn": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/observeOn.js"),
      "rxjs/operator/onErrorResumeNext": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/onErrorResumeNext.js"),
      "rxjs/operator/pairwise": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/pairwise.js"),
      "rxjs/operator/partition": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/partition.js"),
      "rxjs/operator/pluck": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/pluck.js"),
      "rxjs/operator/publish": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/publish.js"),
      "rxjs/operator/publishBehavior": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/publishBehavior.js"),
      "rxjs/operator/publishLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/publishLast.js"),
      "rxjs/operator/publishReplay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/publishReplay.js"),
      "rxjs/operator/race": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/race.js"),
      "rxjs/operator/reduce": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/reduce.js"),
      "rxjs/operator/repeat": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/repeat.js"),
      "rxjs/operator/repeatWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/repeatWhen.js"),
      "rxjs/operator/retry": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/retry.js"),
      "rxjs/operator/retryWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/retryWhen.js"),
      "rxjs/operator/sample": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/sample.js"),
      "rxjs/operator/sampleTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/sampleTime.js"),
      "rxjs/operator/scan": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/scan.js"),
      "rxjs/operator/sequenceEqual": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/sequenceEqual.js"),
      "rxjs/operator/share": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/share.js"),
      "rxjs/operator/shareReplay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/shareReplay.js"),
      "rxjs/operator/single": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/single.js"),
      "rxjs/operator/skip": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/skip.js"),
      "rxjs/operator/skipLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/skipLast.js"),
      "rxjs/operator/skipUntil": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/skipUntil.js"),
      "rxjs/operator/skipWhile": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/skipWhile.js"),
      "rxjs/operator/startWith": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/startWith.js"),
      "rxjs/operator/subscribeOn": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/subscribeOn.js"),
      "rxjs/operator/switch": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/switch.js"),
      "rxjs/operator/switchMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/switchMap.js"),
      "rxjs/operator/switchMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/switchMapTo.js"),
      "rxjs/operator/take": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/take.js"),
      "rxjs/operator/takeLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/takeLast.js"),
      "rxjs/operator/takeUntil": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/takeUntil.js"),
      "rxjs/operator/takeWhile": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/takeWhile.js"),
      "rxjs/operator/throttle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/throttle.js"),
      "rxjs/operator/throttleTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/throttleTime.js"),
      "rxjs/operator/timeInterval": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/timeInterval.js"),
      "rxjs/operator/timeout": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/timeout.js"),
      "rxjs/operator/timeoutWith": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/timeoutWith.js"),
      "rxjs/operator/timestamp": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/timestamp.js"),
      "rxjs/operator/toArray": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/toArray.js"),
      "rxjs/operator/toPromise": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/toPromise.js"),
      "rxjs/operator/window": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/window.js"),
      "rxjs/operator/windowCount": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/windowCount.js"),
      "rxjs/operator/windowTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/windowTime.js"),
      "rxjs/operator/windowToggle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/windowToggle.js"),
      "rxjs/operator/windowWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/windowWhen.js"),
      "rxjs/operator/withLatestFrom": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/withLatestFrom.js"),
      "rxjs/operator/zip": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/zip.js"),
      "rxjs/operator/zipAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operator/zipAll.js"),
      "rxjs/operators/audit": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/audit.js"),
      "rxjs/operators/auditTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/auditTime.js"),
      "rxjs/operators/buffer": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/buffer.js"),
      "rxjs/operators/bufferCount": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/bufferCount.js"),
      "rxjs/operators/bufferTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/bufferTime.js"),
      "rxjs/operators/bufferToggle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/bufferToggle.js"),
      "rxjs/operators/bufferWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/bufferWhen.js"),
      "rxjs/operators/catchError": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/catchError.js"),
      "rxjs/operators/combineAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/combineAll.js"),
      "rxjs/operators/combineLatest": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/combineLatest.js"),
      "rxjs/operators/concat": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/concat.js"),
      "rxjs/operators/concatAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/concatAll.js"),
      "rxjs/operators/concatMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/concatMap.js"),
      "rxjs/operators/concatMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/concatMapTo.js"),
      "rxjs/operators/count": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/count.js"),
      "rxjs/operators/debounce": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/debounce.js"),
      "rxjs/operators/debounceTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/debounceTime.js"),
      "rxjs/operators/defaultIfEmpty": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/defaultIfEmpty.js"),
      "rxjs/operators/delay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/delay.js"),
      "rxjs/operators/delayWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/delayWhen.js"),
      "rxjs/operators/dematerialize": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/dematerialize.js"),
      "rxjs/operators/distinct": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/distinct.js"),
      "rxjs/operators/distinctUntilChanged": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/distinctUntilChanged.js"),
      "rxjs/operators/distinctUntilKeyChanged": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/distinctUntilKeyChanged.js"),
      "rxjs/operators/elementAt": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/elementAt.js"),
      "rxjs/operators/every": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/every.js"),
      "rxjs/operators/exhaust": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/exhaust.js"),
      "rxjs/operators/exhaustMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/exhaustMap.js"),
      "rxjs/operators/expand": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/expand.js"),
      "rxjs/operators/filter": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/filter.js"),
      "rxjs/operators/finalize": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/finalize.js"),
      "rxjs/operators/find": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/find.js"),
      "rxjs/operators/findIndex": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/findIndex.js"),
      "rxjs/operators/first": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/first.js"),
      "rxjs/operators/groupBy": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/groupBy.js"),
      "rxjs/operators/ignoreElements": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/ignoreElements.js"),
      "rxjs/operators/index": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/index.js"),
      "rxjs/operators/isEmpty": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/isEmpty.js"),
      "rxjs/operators/last": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/last.js"),
      "rxjs/operators/map": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/map.js"),
      "rxjs/operators/mapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/mapTo.js"),
      "rxjs/operators/materialize": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/materialize.js"),
      "rxjs/operators/max": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/max.js"),
      "rxjs/operators/merge": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/merge.js"),
      "rxjs/operators/mergeAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/mergeAll.js"),
      "rxjs/operators/mergeMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/mergeMap.js"),
      "rxjs/operators/mergeMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/mergeMapTo.js"),
      "rxjs/operators/mergeScan": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/mergeScan.js"),
      "rxjs/operators/min": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/min.js"),
      "rxjs/operators/multicast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/multicast.js"),
      "rxjs/operators/observeOn": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/observeOn.js"),
      "rxjs/operators/onErrorResumeNext": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/onErrorResumeNext.js"),
      "rxjs/operators/pairwise": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/pairwise.js"),
      "rxjs/operators/partition": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/partition.js"),
      "rxjs/operators/pluck": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/pluck.js"),
      "rxjs/operators/publish": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/publish.js"),
      "rxjs/operators/publishBehavior": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/publishBehavior.js"),
      "rxjs/operators/publishLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/publishLast.js"),
      "rxjs/operators/publishReplay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/publishReplay.js"),
      "rxjs/operators/race": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/race.js"),
      "rxjs/operators/reduce": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/reduce.js"),
      "rxjs/operators/refCount": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/refCount.js"),
      "rxjs/operators/repeat": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/repeat.js"),
      "rxjs/operators/repeatWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/repeatWhen.js"),
      "rxjs/operators/retry": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/retry.js"),
      "rxjs/operators/retryWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/retryWhen.js"),
      "rxjs/operators/sample": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/sample.js"),
      "rxjs/operators/sampleTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/sampleTime.js"),
      "rxjs/operators/scan": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/scan.js"),
      "rxjs/operators/sequenceEqual": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/sequenceEqual.js"),
      "rxjs/operators/share": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/share.js"),
      "rxjs/operators/shareReplay": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/shareReplay.js"),
      "rxjs/operators/single": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/single.js"),
      "rxjs/operators/skip": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/skip.js"),
      "rxjs/operators/skipLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/skipLast.js"),
      "rxjs/operators/skipUntil": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/skipUntil.js"),
      "rxjs/operators/skipWhile": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/skipWhile.js"),
      "rxjs/operators/startWith": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/startWith.js"),
      "rxjs/operators/subscribeOn": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/subscribeOn.js"),
      "rxjs/operators/switchAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/switchAll.js"),
      "rxjs/operators/switchMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/switchMap.js"),
      "rxjs/operators/switchMapTo": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/switchMapTo.js"),
      "rxjs/operators/take": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/take.js"),
      "rxjs/operators/takeLast": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/takeLast.js"),
      "rxjs/operators/takeUntil": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/takeUntil.js"),
      "rxjs/operators/takeWhile": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/takeWhile.js"),
      "rxjs/operators/tap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/tap.js"),
      "rxjs/operators/throttle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/throttle.js"),
      "rxjs/operators/throttleTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/throttleTime.js"),
      "rxjs/operators/timeInterval": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/timeInterval.js"),
      "rxjs/operators/timeout": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/timeout.js"),
      "rxjs/operators/timeoutWith": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/timeoutWith.js"),
      "rxjs/operators/timestamp": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/timestamp.js"),
      "rxjs/operators/toArray": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/toArray.js"),
      "rxjs/operators/window": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/window.js"),
      "rxjs/operators/windowCount": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/windowCount.js"),
      "rxjs/operators/windowTime": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/windowTime.js"),
      "rxjs/operators/windowToggle": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/windowToggle.js"),
      "rxjs/operators/windowWhen": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/windowWhen.js"),
      "rxjs/operators/withLatestFrom": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/withLatestFrom.js"),
      "rxjs/operators/zip": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/zip.js"),
      "rxjs/operators/zipAll": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/zipAll.js"),
      "rxjs/scheduler/Action": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/Action.js"),
      "rxjs/scheduler/AnimationFrameAction": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/AnimationFrameAction.js"),
      "rxjs/scheduler/AnimationFrameScheduler": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/AnimationFrameScheduler.js"),
      "rxjs/scheduler/AsapAction": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/AsapAction.js"),
      "rxjs/scheduler/AsapScheduler": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/AsapScheduler.js"),
      "rxjs/scheduler/AsyncAction": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/AsyncAction.js"),
      "rxjs/scheduler/AsyncScheduler": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/AsyncScheduler.js"),
      "rxjs/scheduler/QueueAction": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/QueueAction.js"),
      "rxjs/scheduler/QueueScheduler": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/QueueScheduler.js"),
      "rxjs/scheduler/VirtualTimeScheduler": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/VirtualTimeScheduler.js"),
      "rxjs/scheduler/animationFrame": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/animationFrame.js"),
      "rxjs/scheduler/asap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/asap.js"),
      "rxjs/scheduler/async": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/async.js"),
      "rxjs/scheduler/queue": path.join(process.cwd(), "/node_modules/rxjs/_esm5/scheduler/queue.js"),
      "rxjs/symbol/iterator": path.join(process.cwd(), "/node_modules/rxjs/_esm5/symbol/iterator.js"),
      "rxjs/symbol/observable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/symbol/observable.js"),
      "rxjs/symbol/rxSubscriber": path.join(process.cwd(), "/node_modules/rxjs/_esm5/symbol/rxSubscriber.js"),
      "rxjs/testing/ColdObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/testing/ColdObservable.js"),
      "rxjs/testing/HotObservable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/testing/HotObservable.js"),
      "rxjs/testing/SubscriptionLog": path.join(process.cwd(), "/node_modules/rxjs/_esm5/testing/SubscriptionLog.js"),
      "rxjs/testing/SubscriptionLoggable": path.join(process.cwd(), "/node_modules/rxjs/_esm5/testing/SubscriptionLoggable.js"),
      "rxjs/testing/TestMessage": path.join(process.cwd(), "/node_modules/rxjs/_esm5/testing/TestMessage.js"),
      "rxjs/testing/TestScheduler": path.join(process.cwd(), "/node_modules/rxjs/_esm5/testing/TestScheduler.js"),
      "rxjs/util/AnimationFrame": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/AnimationFrame.js"),
      "rxjs/util/ArgumentOutOfRangeError": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/ArgumentOutOfRangeError.js"),
      "rxjs/util/EmptyError": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/EmptyError.js"),
      "rxjs/util/FastMap": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/FastMap.js"),
      "rxjs/util/Immediate": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/Immediate.js"),
      "rxjs/util/Map": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/Map.js"),
      "rxjs/util/MapPolyfill": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/MapPolyfill.js"),
      "rxjs/util/ObjectUnsubscribedError": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/ObjectUnsubscribedError.js"),
      "rxjs/util/Set": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/Set.js"),
      "rxjs/util/TimeoutError": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/TimeoutError.js"),
      "rxjs/util/UnsubscriptionError": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/UnsubscriptionError.js"),
      "rxjs/util/applyMixins": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/applyMixins.js"),
      "rxjs/util/assign": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/assign.js"),
      "rxjs/util/errorObject": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/errorObject.js"),
      "rxjs/util/identity": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/identity.js"),
      "rxjs/util/isArray": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/isArray.js"),
      "rxjs/util/isArrayLike": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/isArrayLike.js"),
      "rxjs/util/isDate": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/isDate.js"),
      "rxjs/util/isFunction": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/isFunction.js"),
      "rxjs/util/isNumeric": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/isNumeric.js"),
      "rxjs/util/isObject": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/isObject.js"),
      "rxjs/util/isPromise": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/isPromise.js"),
      "rxjs/util/isScheduler": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/isScheduler.js"),
      "rxjs/util/noop": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/noop.js"),
      "rxjs/util/not": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/not.js"),
      "rxjs/util/pipe": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/pipe.js"),
      "rxjs/util/root": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/root.js"),
      "rxjs/util/subscribeToResult": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/subscribeToResult.js"),
      "rxjs/util/toSubscriber": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/toSubscriber.js"),
      "rxjs/util/tryCatch": path.join(process.cwd(), "/node_modules/rxjs/_esm5/util/tryCatch.js"),
      "rxjs/operators": path.join(process.cwd(), "/node_modules/rxjs/_esm5/operators/index.js")
    },
    "mainFields": [
      "browser",
      "module",
      "main"
    ]
  },
  "resolveLoader": {
    "modules": [
      "./node_modules",
      "./node_modules"
    ]
  },
  "entry": {
    "main": [
      "./src/main.ts"
    ],
    "polyfills": [
      "./src/polyfills.ts"
    ],
    "styles": [
      "./src/assets/stylesheets/styles.scss"
    ]
  },
  "output": {
    "path": path.join(process.cwd(), "dist/" + env.locale),
    "publicPath": deployUrl,
    "filename": "[name].[chunkhash:20].bundle.js",
    "chunkFilename": "[id].[chunkhash:20].chunk.js",
    "crossOriginLoading": false
  },
  "module": {
    "rules": [
      {
        "test": /\.html$/,
        "loader": "raw-loader"
      },
      {
        "test": /\.(eot|svg|cur)$/,
        "loader": "file-loader",
        "options": {
          "name": "[name].[hash:20].[ext]",
          "limit": 10000
        }
      },
      {
        "test": /\.(jpg|png|webp|gif|otf|ttf|woff|woff2|ani)$/,
        "loader": "url-loader",
        "options": {
          "name": "[name].[hash:20].[ext]",
          "limit": 10000
        }
      },
      {
        "test": /\.js$/,
        "use": [
          {
            "loader": "@angular-devkit/build-optimizer/webpack-loader",
            "options": {
              "sourceMap": false
            }
          }
        ]
      },
      {
        "exclude": [
          path.join(process.cwd(), "src/assets/stylesheets/styles.scss")
        ],
        "test": /\.scss$|\.sass$/,
        "use": [
          "exports-loader?module.exports.toString()",
          {
            "loader": "css-loader",
            "options": {
              "sourceMap": false,
              "importLoaders": 1
            }
          },
          {
            "loader": "postcss-loader",
            "options": {
              "ident": "postcss",
              "plugins": postcssPlugins
            }
          },
          {
            "loader": "sass-loader",
            "options": {
              "sourceMap": false,
              "precision": 8,
              "includePaths": []
            }
          }
        ]
      },
      {
        "include": [
          path.join(process.cwd(), "src/assets/stylesheets/styles.scss")
        ],
        "test": /\.scss$|\.sass$/,
        "loaders": screenStyleExtractor.extract({
          "use": [
            {
              "loader": "css-loader",
              "options": {
                "sourceMap": false,
                "importLoaders": 1
              }
            },
            {
              "loader": "postcss-loader",
              "options": {
                "ident": "postcss",
                "plugins": postcssPlugins
              }
            },
            {
              "loader": "sass-loader",
              "options": {
                "sourceMap": false,
                "precision": 8,
                "includePaths": []
              }
            }
          ],
          "publicPath": ""
        })
      },
      {
        "test": /(?:\.ngfactory\.js|\.ngstyle\.js|\.ts)$/,
        "use": [
          {
            "loader": "@angular-devkit/build-optimizer/webpack-loader",
            "options": {
              "sourceMap": false
            }
          },
          "@ngtools/webpack"
        ]
      }
    ]
  },
  "plugins": [
    new NoEmitOnErrorsPlugin(),
    new CopyWebpackPlugin([
      {
        "context": "src",
        "to": "",
        "from": {
          "glob": "assets/locale/*",
          "dot": true
        }
      },
      {
        "context": "src",
        "to": "",
        "from": {
          "glob": "favicon.ico",
          "dot": true
        }
      }
    ], {
        "ignore": [
          ".gitkeep"
        ],
        "debug": "warning"
      }),
    new ProgressPlugin(),
    new CircularDependencyPlugin({
      "exclude": /(\\|\/)node_modules(\\|\/)/,
      "failOnError": false
    }),
    new HtmlWebpackPlugin({
      "template": "./src/index.html",
      "filename": "./index.html",
      "hash": false,
      "inject": true,
      "compile": true,
      "favicon": false,
      "minify": {
        "caseSensitive": true,
        "collapseWhitespace": true,
        "keepClosingSlash": true
      },
      "cache": true,
      "showErrors": true,
      "chunks": "all",
      "excludeChunks": [],
      "title": "Stores Account",
      "xhtml": true,
      "chunksSortMode": function sort(left, right) {
        let leftIndex = entryPoints.indexOf(left.names[0]);
        let rightindex = entryPoints.indexOf(right.names[0]);
        if (leftIndex > rightindex) {
          return 1;
        }
        else if (leftIndex < rightindex) {
          return -1;
        }
        else {
          return 0;
        }
      }
    }),
    new BaseHrefWebpackPlugin({
      "baseHref": baseHref
    }),
    new CommonsChunkPlugin({
      "name": [
        "inline"
      ],
      "minChunks": null
    }),
    new CommonsChunkPlugin({
      "name": [
        "main"
      ],
      "minChunks": 2,
      "async": "common"
    }),
    screenStyleExtractor,
    new SuppressExtractedTextChunksWebpackPlugin(),
    new EnvironmentPlugin({
      "NODE_ENV": "production"
    }),
    new HashedModuleIdsPlugin({
      "hashFunction": "md5",
      "hashDigest": "base64",
      "hashDigestLength": 4
    }),
    new ModuleConcatenationPlugin({}),
    new UglifyJsPlugin({
      "test": /\.js$/i,
      "extractComments": false,
      "sourceMap": false,
      "cache": false,
      "parallel": false,
      "uglifyOptions": {
        "output": {
          "ascii_only": true,
          "comments": false
        },
        "ecma": 5,
        "warnings": false,
        "ie8": false,
        "mangle": true,
        "compress": {
          "pure_getters": true,
          "passes": 3
        }
      }
    }),
    new LicenseWebpackPlugin({
      "licenseFilenames": [
        "LICENSE",
        "LICENSE.md",
        "LICENSE.txt",
        "license",
        "license.md",
        "license.txt"
      ],
      "perChunkOutput": false,
      "outputTemplate": path.join(process.cwd(), "/node_modules/license-webpack-plugin/output.template.ejs"),
      "outputFilename": "3rdpartylicenses.txt",
      "suppressErrors": true,
      "includePackagesWithoutLicense": false,
      "abortOnUnacceptableLicense": false,
      "addBanner": false,
      "bannerTemplate": "/*! 3rd party license information is available at <%- filename %> */",
      "includedChunks": [],
      "excludedChunks": [],
      "additionalPackages": [],
      "pattern": /^(MIT|ISC|BSD.*)$/
    }),
    new LinkMediaHTMLPlugin(),
    new PurifyPlugin(),
    new AngularCompilerPlugin({
      "mainPath": "main.ts",
      "i18nInFile": "src/assets/locale/messages." + env.locale + ".xlf",
      "i18nInFormat": "xlf",
      "locale": env.locale,
      "platform": 0,
      "hostReplacementPaths": {
        "./environments/environment.ts": "environments/environment.prod.ts"
      },
      "sourceMap": false,
      "tsConfigPath": "src/tsconfig.app.json",
      "compilerOptions": {}
    })
  ],
  "node": {
    "fs": "empty",
    "global": true,
    "crypto": "empty",
    "tls": "empty",
    "net": "empty",
    "process": true,
    "module": false,
    "clearImmediate": false,
    "setImmediate": false
  },
  "devServer": {
    "historyApiFallback": true
  }
}
};
