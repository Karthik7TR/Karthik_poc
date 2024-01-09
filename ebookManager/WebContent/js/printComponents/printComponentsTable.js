function PrintComponentsTable(
        jsGridContainerId,
        editParam,
        superUserParam,
        colorPrintComponentTableParam,
        editBookDefinitionFormId
) {
    var printComponentsDataId = jsGridContainerId + "PrintComponentsData";
    var gridContainerDataTbodyId = jsGridContainerId + " .jsgrid-grid-body tbody";
    var editable = editParam == "true";
    var isSuperUser = superUserParam == "true";
    var colorPrintComponentTable = colorPrintComponentTableParam == "true";

    var printComponents = $.parseJSON($(printComponentsDataId).val());

    var gridManager = {
            reloadData: function(newPrintComponents) {
                printComponents = $.parseJSON($('<div/>').html(newPrintComponents).text());
                
                $(jsGridContainerId).jsGrid("option", "data", printComponents);
                $(printComponentsDataId).attr('value', JSON.stringify($(jsGridContainerId).data("JSGrid").data));
                
                gridManager.reorderIndexes();
                gridManager.colorLines();
            },

            //Method to shift idexes of elements.
            //Is usualy used after manual changes in table
            exchangeIndexes: function() {
                for (i = 0; i < printComponents.length; i++) {
                    printComponents[i].componentOrder = i + 1;
                    gridManager.sortGridByComponentOrder();
                }
            },

            colorPrintComponents: function() {
                var $gridData = $(gridContainerDataTbodyId);
                items = $.map($gridData.find("tr"), function(row) {
                    return $(row);
                });
                for(i = 0; i < printComponents.length; i++) {
                	if (!gridManager.isSplitter(printComponents[i])) {
                		if (!printComponents[i].componentInArchive) {
                			$(".client-" + i + " > td").css("background","#ff7D7D");
                            $(".print_component_legend.bundle_not_found").show();
                		} else if (printComponents[i].supplement) {
                			$(".client-" + i + " > td").css("background","yellow");
                            $(".print_component_legend.supplement").show();
                		}
                	}
                }
            },

            refreshingRowActivities: function() {
                var $gridData = $(gridContainerDataTbodyId);
                if (editable) {
                    $gridData.sortable({
                        update: function(e, ui) {
                            $gridData.sortable({cancel:'.jsgrid-edit-row'});
                            // array of indexes
                            var clientIndexRegExp = /\s*client-(\d+)\s*/;
                            var indexes = $.map($gridData.sortable("toArray", {
                                attribute: "class"
                            }), 
                            function(classes) {
                                return clientIndexRegExp.exec(classes)[1];
                            });
                            // arrays of items
                            var items = $.map($gridData.find("tr"), function(row) {
                                return $(row).data("JSGridItem");
                            });
                            printComponents = items;
                            gridManager.exchangeIndexes();
                            gridManager.sortGridByComponentOrder();
                            $('#up_button, #down_button').removeAttr('disabled');
                        }
                    });
                }
                $('#up_button, #down_button').removeAttr('disabled');
            },

            //Method to shift idexes of elements and sort table.
            //Is usualy used after manual changes in table
            sortGridByComponentOrder: function() {
              $(jsGridContainerId).jsGrid("sort", {
                  field: "componentOrder",
                  order: "asc"
              });
            },

            reorderIndexes: function() {
                printComponents.sort(function(a, b) {
                    return parseInt(a.componentOrder) - parseInt(b.componentOrder);
                });
                gridManager.exchangeIndexes();
            },
            
            isSplitter: function(item) {
                return item.splitter == true;// || (item.splitter != null && item.splitter == "Y");
            },
            
            colorLines: function() {
                printComponents.forEach(function(printComponent, index) {
                    if (gridManager.isSplitter(printComponent)) {
                        $(jsGridContainerId + " .client-" + index + " > td").css("background","#7dffff");
                    }
                });
            },
            
            getCurrentComponentsCount: function() {
                var splittersCount = 0;
                var componentsCount = 0;
                for(var i = 0; i < printComponents.length; i++) {
                    if(gridManager.isSplitter(printComponents[i])) {
                        splittersCount++;
                    } else {
                        componentsCount++;
                    }
                }
                return {
                    splitters: splittersCount,
                    components: componentsCount,
                    total: printComponents.length
                };
            },

            jsGridMainObject: function() {
                $(jsGridContainerId).jsGrid({
                    width: "100%",
                    autoload: true,
                    deleteConfirm: "Do you really want to delete the client?",
                    editing: editable && isSuperUser,
                    inserting: editable && isSuperUser,
                    
                    rowClass: function(item, itemIndex) {
                      return "client-" + itemIndex;
                    },
                    
                    rowClick: function(args) {
                        if(this.editing && !gridManager.isSplitter(args.item)) {
                            this.editItem($(args.event.target).closest("tr"));
                        }
                    },
                    
                    onItemEditing: function(args){
                        $gridData.sortable({cancel:'.jsgrid-edit-row, .ui-sortable, .jsgrid-cell, .jsgrid-grid-body tbody, .jsgrid-row, .jsgrid-alt-row'});
                        $('#up_button, #down_button').prop('disabled','disabled');
                        args.item.componentName = htmlEnDeCode.htmlDecode(args.item.componentName);
                    },
                    
                    onItemUpdated: function(args) {
                        if ( $(".jsgrid-edit-row" ).length == 0 ) {
                            $gridData.sortable({cancel:'.jsgrid-edit-row'});
                            $('#up_button, #down_button').removeAttr('disabled');
                        }

                        var jsGrid = $(jsGridContainerId).jsGrid("option", "data");
                        for (i = 0; i < jsGrid.length; i++) {
                            if(args.item.materialNumber == jsGrid[i].materialNumber){
                                jsGrid[i].componentName = htmlEnDeCode.htmlEncode(args.item.componentName);
                                gridManager.reorderIndexes();
                            }
                        }
                    },

                    controller: {
                        //<!--Method to initialize values of data table.-->
                        init: function() {
                            if ($(editBookDefinitionFormId).length > 0) {
                                $(document).on('submit', editBookDefinitionFormId, function() {
                                    //<!--before commits printComponent need to be stringify-->
                                    $(printComponentsDataId).attr('value', JSON.stringify($(jsGridContainerId).data("JSGrid").data));
                                });
                            }
                        },
                        loadData: function() {
                            this.init();
                            $(".jsgrid-edit-row" ).remove();
                            return printComponents;
                        }
                    },
                    
                    fields: [{
                        // <!--Custom field wich does not need no hold data.
                        // We use no type to implement custom element without transfer data-->
                        width: 80,
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
                                   disabled: '$(".jsgrid-edit-row" ).length',
                                   click: function() {
                                       var $gridData = $(gridContainerDataTbodyId);
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
                                           gridManager.reorderIndexes();
                                           $(jsGridContainerId).jsGrid("refresh");
                                           $('#up_button, #down_button').removeAttr('disabled');
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
                                    disabled: '$(".jsgrid-edit-row" ).length',
                                    click: function() {
                                        $gridData = $(gridContainerDataTbodyId);
                                        items = $.map($gridData.find("tr"), function(row) {
                                            return $(row).data("JSGridItem");
                                        });
                                        if (item.componentOrder < items.length) {
                                            //exchange indexes
                                            currentElementIndex = item.componentOrder - 1;
                                            items[currentElementIndex].componentOrder++;
                                            items[currentElementIndex + 1].componentOrder--;
                                            gridManager.reorderIndexes();
                                            $(jsGridContainerId).jsGrid("refresh");
                                            $('#up_button, #down_button').removeAttr('disabled');
                                        }
                                    },
                                    onload: function(){
                                       $gridData = $(gridContainerDataTbodyId);
                                       items = $.map($gridData.find("tr"), function(row) {
                                           return $(row).data("JSGridItem");
                                       });
                                       if(item.componentOrder + 1 > printComponents.length){
                                           this.style.display = 'none';
                                       }
                                    }
                                });
                            });
                            return customDiv;
                        },
                        visible: editable
                    }, 
                    {
                        name: "componentOrder",
                        title: "Order",
                        width: 50,
                        editing: false,
                        sorting: false,
                        sorter: "numberAsString",
                        align: "center"
                    }, 
                    {
                        name: "materialNumber",
                        title: "Material Number",
                        type: "text",
                        width: 100,
                        validate: [
                            "required",
                            {
                                message: "Only numbers are allowed for Material",
                                validator: function(value, item) {
                                    return gridManager.isSplitter(item) || !isNaN(value) ;
                                }
                            }],
                        insertTemplate: function() {
                            var $result = jsGrid.fields.text.prototype.insertTemplate.call(this);
                            $result.attr("id", "materialNumberId");
                            return $result;
                        }
                    }, 
                    {
                        name: "componentName",
                        title: "SAP Description",
                        type: "text",
                        width: 120,
                        validate: ["required", {
                        		validator: "maxLength", 
                        		message: "SAP Description field value should not be longer than 40 characters",
                        		param: 40
                        }],
                        css: "sap-description-field",
                        insertTemplate: function() {
                            var $result = jsGrid.fields.text.prototype.insertTemplate.call(this);
                            $result.attr("id", "componentNameId");
                            return $result;
                        }
                    },
                    {
                       type: "control",
                       editButton: false,
                       modeSwitchButton: false,
                       deleteButton: true,
                       visible: editable && isSuperUser,
                       _createCancelEditButton: function() {
                           var $result = jsGrid.fields.control.prototype._createCancelEditButton.apply(this, arguments);
                           $result.on("click", function() {
                               var $gridData = $(gridContainerDataTbodyId);
                               $(".jsgrid-edit-row" ).remove();
                               $("tr[style='display: none;']").css("display","");
                               $(".jsgrid-filter-row" ).remove();
                               $gridData.sortable({cancel:'.jsgrid-edit-row'});
                               $('#up_button, #down_button').removeAttr('disabled');
                           });
                                  return $result;
                       },
                       headerTemplate: function() {
                           return $("<button>").attr("type", "button")
                              .text("Split item")
                              .click(function(event) {
                                 $(jsGridContainerId).jsGrid("insertItem", {
                                     materialNumber: "-------------",
                                     componentName: "SPLITTER",
                                     splitter: true
                                 });
                              });
                       }
                    }],
                    
                    onRefreshed: function() {
                        gridManager.refreshingRowActivities();
                        gridManager.colorLines();
                    },
                    
                    onItemDeleted: function() {
                        var $gridData = $(gridContainerDataTbodyId);
                        items = $.map($gridData.find("tr"), function(row) {
                            return $(row).data("JSGridItem");
                        });
                        printComponents = items;
                        gridManager.exchangeIndexes();
                        gridManager.sortGridByComponentOrder();
                    },
                    
                    onItemInserting: function(args) {
                        // <!-- cancel insertion of the item with empty 'name' field-->
                        if ((args.item.materialNumber === "") || (args.item.componentName === "")) {
                            args.cancel = true;
                            alert("Specify the materialNumber and componentName of the item!");
                        } else {                    	
                            var gridData = $(jsGridContainerId).jsGrid("option", "data");
                            if (!gridManager.isSplitter(args.item)) {
                                for (i = 0; i < gridData.length; i++) {
                                    if(args.item.materialNumber == gridData[i].materialNumber){
                                        args.cancel = true;
                                        alert(args.item.materialNumber+" Material Number is duplicate.");
                                    }
                                }
                                escapeEditedFields(gridData);
                                args.item.componentName = htmlEnDeCode.htmlEncode(args.item.componentName);
                            } else {
                                var componentsCounts = gridManager.getCurrentComponentsCount();
                                if (componentsCounts.components <= componentsCounts.splitters + 1) {
                                    args.cancel = true;
                                    alert("Number of splitters cannot be equal or greater than number of print components");
                                }
                            }
                            
                            if(!args.cancel){
                                item = args.item;
                                var $gridData = $(gridContainerDataTbodyId);
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
                        }
                    },

                    onItemUpdating: function(args) {
                        var arg = args;
                        var gridData = $(jsGridContainerId).jsGrid("option", "data");
                          for (i = 0; i < gridData.length; i++) {
                              if (args.item.materialNumber == gridData[i].materialNumber) {
                                  if (!isNaN(args.item.componentOrder) && 
                                      args.item.componentOrder != gridData[i].componentOrder) {
                                      args.cancel = true;
                                      alert("Material Number already exists " + args.item.materialNumber);
                                  }
                                  gridData[i].componentName = htmlEnDeCode.htmlEncode(args.item.componentName);
                              }
                          }
                          gridManager.exchangeIndexes();
                    },

                    onItemInserted: function(insertedArgs) {
                        var tempInsertedPrintComponent = {
                            componentOrder: Number(insertedArgs.item.componentOrder),
                            materialNumber: insertedArgs.item.materialNumber,
                            componentName: insertedArgs.item.componentName
                        };
                        insertedArgs.item = tempInsertedPrintComponent;
                        var $gridData = $(gridContainerDataTbodyId);
                        items = $.map($gridData.find("tr"), function(row) {
                            return $(row).data("JSGridItem");
                        });
                        printComponents = items;
                        gridManager.exchangeIndexes();
                        $('#up_button, #down_button').removeAttr('disabled');
                    }
                });
                if (colorPrintComponentTable) {
                    gridManager.colorPrintComponents();
                }
                $('#up_button, #down_button').removeAttr('disabled');
            }
        };

        $(jsGridContainerId).get(0).gridManager = gridManager;

        //sort rows in case if data is not sorted-ordered
        gridManager.reorderIndexes();
        
        $(gridManager.jsGridMainObject);        
}

function escapeEditedFields(gridData) {
    if (Array.prototype.slice.call(gridData).length > 0) {
        var rowToEscape = $('#jsGrid tr')
            .toArray()
            .filter(function (el) { return el.style.display !== 'none'; })
            .slice(2)
            .reduce(function (prev, current, index) {
                var textInputs = Array.prototype.filter.call(current.getElementsByTagName('input'), 
                    function (el) { return el.type === 'text'; });
                var isBeingEdited = textInputs.length > 0;
                if (isBeingEdited) {
                    return prev + index + 1;
                }
                return prev;
            }, -1);
        if (rowToEscape >= 0) {
            gridData[rowToEscape].componentName = htmlEnDeCode.htmlEncode(gridData[rowToEscape].componentName);
        }
    }
}