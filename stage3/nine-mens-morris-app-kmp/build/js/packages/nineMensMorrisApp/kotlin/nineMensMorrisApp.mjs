
import * as Li9za2lrby5tanM from './skiko.mjs';
import { instantiate } from './nineMensMorrisApp.uninstantiated.mjs';


const exports = (await instantiate({
    './skiko.mjs': Li9za2lrby5tanM
})).exports;

export const {
    memory,
    _initialize,
    startUnitTests
} = exports;

