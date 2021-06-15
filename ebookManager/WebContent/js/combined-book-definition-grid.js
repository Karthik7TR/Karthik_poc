const CHECK_HTML_SYMBOL = "\u2714";
$(function () {
    const GridCheckbox = function (config) {
        jsGrid.Field.call(this, config);
    };

    function handleCheckEvent(input) {
        if (input.is(":checked")) {
            $('tr:not(.jsgrid-insert-row) td.grid-checkbox').html('');
            const data = $('#jsGrid').jsGrid('option', 'data');
            data.forEach(item => item.isPrimary = "");
        }
        return input.is(":checked") ? CHECK_HTML_SYMBOL : "";
    }

    function createCheckboxWithState(text) {
        const isCheckedText = text === CHECK_HTML_SYMBOL ? 'checked' : '';
        return this._insertInput = $('<input type="checkbox" ' + isCheckedText + '>');
    }

    GridCheckbox.prototype = new jsGrid.Field({
        css: 'grid-checkbox',
        align: 'center',

        itemTemplate: function (value) {
            return value;
        },

        insertTemplate: function (value) {
            return this._insertInput = createCheckboxWithState(value);
        },

        editTemplate: function (value) {
            return this._editInput = createCheckboxWithState(value);
        },

        insertValue: function () {
            return handleCheckEvent(this._insertInput);
        },

        editValue: function () {
            return handleCheckEvent(this._editInput);
        }
    });

    $(document).on('submit', '#combinedBookDefinitionForm', function () {
        $('#combined-book-definition-sources').attr('value', JSON.stringify($.map($('#jsGrid .jsgrid-grid-body tbody').find('tr'), function (row) {
            return $(row).data('JSGridItem');
        }).map((item, index) => {
            return {
                id: item.sourceId,
                bookDefinition: {
                    fullyQualifiedTitleId: item.fullyQualifiedTitleId
                },
                isPrimarySource: item.isPrimary === CHECK_HTML_SYMBOL,
                sequenceNum: index
            }
        })));
    });

    jsGrid.fields.customCheckbox = GridCheckbox;
    $("#jsGrid").jsGrid({
        height: '70%',
        width: '50%',
        autoload: true,
        editing: true,
        inserting: true,

        rowClass: function (item, itemIndex) {
            return 'client-' + itemIndex;
        },

        controller: {
            loadData: function () {
                const data = $.parseJSON($('#combined-book-definition-sources').val());
                return data.map(item => {
                    return {
                        ebookDefinitionId: item.bookDefinition.ebookDefinitionId,
                        fullyQualifiedTitleId: item.bookDefinition.fullyQualifiedTitleId,
                        sourceId: item.id,
                        isPrimary: item.primarySource ? CHECK_HTML_SYMBOL : '',
                        order: item.sequenceNum
                    }
                }).sort(function (a, b) {
                    return a.order - b.order;
                })
            }
        },

        fields: [
            {name: 'fullyQualifiedTitleId', type: 'text', title: 'Title ID', width: 150},
            {name: 'isPrimary', type: 'customCheckbox'},
            {type: 'control', modeSwitchButton: false}
        ],

        onRefreshed: function () {
            const sourcesData = $('#jsGrid .jsgrid-grid-body tbody');
            sourcesData.sortable({
                update: function (e, ui) {
                    const clientIndexRegExp = /\s*client-(\d+)\s*/;
                    $.map(sourcesData.sortable('toArray', {attribute: 'class'}), function (classes) {
                        return clientIndexRegExp.exec(classes)[1];
                    });
                }
            });
        }
    });
})