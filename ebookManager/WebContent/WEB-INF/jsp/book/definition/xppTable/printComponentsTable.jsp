<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

    <script src="js/jsgrid/db.js"></script>
    <script src="js/jsgrid/src/jsgrid.core.js"></script>
    <script src="js/jsgrid/src/jsgrid.load-indicator.js"></script>
    <script src="js/jsgrid/src/jsgrid.load-strategies.js"></script>
    <script src="js/jsgrid/src/jsgrid.sort-strategies.js"></script>
    <script src="js/jsgrid/src/jsgrid.field.js"></script>
    <script src="js/jsgrid/src/fields/jsgrid.field.text.js"></script>
    <script src="js/jsgrid/src/fields/jsgrid.field.control.js"></script>
    <script src="js/jsgrid/src/jsgrid.validation.js"></script>

    <style>
        .hasDatepicker {
            width: 100px;
            text-align: center;
        }
        .ui-datepicker * {
            font-family: 'Helvetica Neue Light', 'Open Sans', Helvetica;
            font-size: 14px;
            font-weight: 300 !important;
        }
        #jsGrid input {
            width: 100%;
        }
        #jsGrid input[type='button'] {
            width: 0;
            padding-right: 10px;
        }
    </style>

    <div id="jsGrid"></div>

    <input type='hidden' id='printComponents' name='printComponents' value='${form.printComponents}'>

    <script>
        //read recieved data to printComponents variable
        printComponents = $.parseJSON($('#printComponents').val());

        //sort rows in case if data is not sorted-ordered
        reorderIndexes();

        //Method to shift idexes of elements.
        //Is usualy used after manual changes in table
        function exchangeIndexes() {
            for (i = 0; i < printComponents.length; i++) {
                printComponents[i].componentOrder = i + 1;

                sortGridByComponentOrder();
            }
        }

        //Method to shift idexes of elements and sort table.
        //Is usualy used after manual changes in table

        function sortGridByComponentOrder(){
          $("#jsGrid").jsGrid("sort", {
              field: "componentOrder",
              order: "asc"
          });
        }

        function reorderIndexes() {

            printComponents.sort(function(a, b) {
                return a.componentOrder - b.componentOrder
            })
            exchangeIndexes();
        }


        $(function() {
            $("#jsGrid").jsGrid({
                width: "100%",
                autoload: true,
                deleteConfirm: "Do you really want to delete the client?",
                editing: ${param.edit},
                inserting: ${param.edit},
                paging: true,

                rowClass: function(item, itemIndex) {
                  return "client-" + itemIndex;
                  },

                controller: {
                    //<!--Method to initialize values of data table.-->
                    init: function() {

                        if ($("#editBookDefinitionForm").length > 0) {
                            $(document).on('submit', '#editBookDefinitionForm', function() {
                                <!--before commits printComponent need to be stringify-->
                                $('#printComponents').attr('value', JSON.stringify($("#jsGrid").data("JSGrid").data));
                            });
                        }
                    },
                    loadData: function() {
                        this.init();
                        return printComponents;
                    }
                },
                fields: [{
                    // <!--Custom field wich does not need no hold data.
                    // We use no type to implement custom element without transfer data-->
                    width: 60,
                    inserting: false,
                    itemTemplate: function(_, item, items) {
                        var customDiv = $("<div>");
                        var dottedBorderDiv = document.createElement('div');
                        dottedBorderDiv.id = "dBorder";
                        dottedBorderDiv.innerHTML = dottedBorderDiv.innerHTML + '&nbsp;';
                        dottedBorderDiv.style.borderLeft = "dotted #BBBBBB";
                        dottedBorderDiv.style.cssFloat = "left";
                        customDiv.append(dottedBorderDiv);
                        customDiv.append(function() {

                           return $('<button/>', {
                            text: "\u25B2",
                            id: 'up_button',
                            click: function() {
                                var $gridData = $("#jsGrid .jsgrid-grid-body tbody");
                                items = $.map($gridData.find("tr"), function(row) {
                                    return $(row).data("JSGridItem");
                                });
                                // trying to move up element
                                // Compare  componentOrder with 1 because this arrow buttons operates
                                // with all elements except the first one-->
                                if (item.componentOrder > 1) {
                                    //exchange indexes
                                    currentElementIndex = item.componentOrder - 1;
                                    items[currentElementIndex].componentOrder--;
                                    items[currentElementIndex - 1].componentOrder++;
                                    reorderIndexes();
                                    $("#grid").jsGrid("refresh");
                                }
                            },
                            onload: function(){
                              if(item.componentOrder < 2){
                                this.style.display = 'none';
                              }
                            }
                          });

                        });

                        customDiv.append(function() {
                            // <!--down arrow button-->
                            return $('<button/>', {
                             text: "\u25BC",
                             id: 'down_button',
                             click: function() {
                                 $gridData = $("#jsGrid .jsgrid-grid-body tbody");
                                 items = $.map($gridData.find("tr"), function(row) {
                                     return $(row).data("JSGridItem");
                                 });

                                 if (item.componentOrder < items.length) {
                                     //exchange indexes
                                     currentElementIndex = item.componentOrder - 1;
                                     items[currentElementIndex].componentOrder++;
                                     items[currentElementIndex + 1].componentOrder--;
                                     reorderIndexes();
                                     $("#grid").jsGrid("refresh");
                                 }
                             },
                             onload: function(){
                               $gridData = $("#jsGrid .jsgrid-grid-body tbody");
                               items = $.map($gridData.find("tr"), function(row) {
                                   return $(row).data("JSGridItem");
                               });
                               if(item.componentOrder+1 > printComponents.length){
                                 this.style.display = 'none';
                               }
                             }
                           });
                        });
                        return customDiv;
                    }
                    ,
                    visible: ${param.edit}
                    }
                , {
                    name: "componentOrder",
                    title: "Order",
                    width: 40,
                    sorter: "numberAsString",
                    //validate: "required",
                    editing: false
                }, {
                    name: "materialNumber",
                    title: "Material Number",
                    type: "text",
                    width: 100,
                    validate: [
                              "required",
                              {
                               message: "Only numbers are allowed for Material",
                               validator: function(value, item) {
                                  return !isNaN(value) ;
                              }
                              }
                          ]
                }, {
                    name: "componentName",
                    title: "Component Name",
                    type: "text",
                    width: 80,
                    validate: "required"
                },
                {
                   type: "control",
                   editButton: false,
                   modeSwitchButton: false,
                   deleteButton: true,
                   visible: ${param.edit}
               }

              ],
                onRefreshed: function() {

                    var $gridData = $("#jsGrid .jsgrid-grid-body tbody");

                    if(${param.edit}){
                      $gridData.sortable({
                        update: function(e, ui) {
                            // array of indexes
                            var clientIndexRegExp = /\s*client-(\d+)\s*/;
                            var indexes = $.map($gridData.sortable("toArray", {
                                attribute: "class"
                            }), function(classes) {
                                return clientIndexRegExp.exec(classes)[1];
                            });

                            // arrays of items
                            var items = $.map($gridData.find("tr"), function(row) {
                                return $(row).data("JSGridItem");
                            });
                            // console && console.log("Reordered items", items);
                            console.log("Inside sortable");
                            printComponents = items;
                            exchangeIndexes();
                            sortGridByComponentOrder();
                        }
                    });
                  }

                },
                onItemDeleted: function() {
                    var $gridData = $("#jsGrid .jsgrid-grid-body tbody");
                    items = $.map($gridData.find("tr"), function(row) {
                        return $(row).data("JSGridItem");
                    });
                    printComponents = items;
                    exchangeIndexes();
                    sortGridByComponentOrder();
                },
                onItemInserting: function(args) {
                    // <!-- cancel insertion of the item with empty 'name' field-->
                    if ((args.item.materialNumber === "") || (args.item.componentName === "")) {
                        args.cancel = true;
                        alert("Specify the materialNumber and componentName of the item!");
                    } else {
                        item = args.item;
                        var $gridData = $("#jsGrid .jsgrid-grid-body tbody");
                        items = $.map($gridData.find("tr"), function(row) {
                            return $(row).data("JSGridItem");
                        });
                        var tempPrintComponent = {
                            componentOrder: Number(item.componentOrder),
                            materialNumber: item.materialNumber,
                            componentName: item.componentName
                        };
                        item = tempPrintComponent;
                        args.item = tempPrintComponent;
                        items[items.length - 1] = tempPrintComponent;
                    }

                },

                onItemInserted: function(insertedArgs) {
                    var tempInsertedPrintComponent = {
                        componentOrder: Number(insertedArgs.item.componentOrder),
                        materialNumber: insertedArgs.item.materialNumber,
                        componentName: insertedArgs.item.componentName
                    };
                    insertedArgs.item = tempInsertedPrintComponent;
                    var $gridData = $("#jsGrid .jsgrid-grid-body tbody");
                    items = $.map($gridData.find("tr"), function(row) {
                        return $(row).data("JSGridItem");
                    });
                    printComponents = items;
                    exchangeIndexes();
                }
            });
        });
    </script>
