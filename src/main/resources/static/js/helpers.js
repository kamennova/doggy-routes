const hide = (elem) => elem.classList.add('hidden');
const show = (elem) => elem.classList.remove('hidden');

const close = (elem) => elem.classList.add('closed');
const open = (elem) => elem.classList.remove('closed');

const formatOL = (geom) => geom.transform('EPSG:4326', 'EPSG:3857');
const formatCommon = (geom) => geom.transform('EPSG:3857', 'EPSG:4326');

const goTo = (page) => document.location.href = page;
