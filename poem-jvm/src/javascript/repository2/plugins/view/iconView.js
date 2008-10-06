/**
 * Copyright (c) 2008
 * Willi Tscheschner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/

// define plugin namespace

if(!Repository.Plugins) Repository.Plugins = {};

Repository.Plugins.IconView = {
	
	
	construct: function(facade) {
		this.name = Repository.I18N.IconView.name;
		arguments.callee.$.construct.apply(this, arguments); // call superclass constructor
		this.icon = '/backend/images/silk/table.png';
		this.numOfDisplayedModels = 10;
		
		// define required data uris
		this.dataUris = ["/meta"];
		
	},
	
	
	
	render : function(modelData) {
				
		if( this.myPanel ){
			this.panel.remove( this.myPanel )
		}
		
		
		var data = [];
		modelData.each(function( pair ){
			var stencilset = pair.value.type;
			// Try to display stencilset title instead of uri
			this.facade.modelCache.getModelTypes().each(function(type){
				if (stencilset == type.namespace) {
					stencilset = type.title;
					return;
				}
			}.bind(this));
			
			data.push( [ pair.key, pair.value.thumbnailUri, pair.value.title, stencilset, pair.value.author || 'Unknown' ] )
		}.bind(this));
		
		var store = new Ext.data.SimpleStore({
	        fields	: ['id', 'icon', 'title', 'type', 'author'],
	        data	: data
	    });
	
	    this.myPanel = new Ext.Panel({
	        items		: new DataGridPanel({store: store, listeners:{selectionchange:this._onSelectionChange.bind(this), dblclick:this._onDblClick.bind(this)}})
	    });

		this.panel.add( this.myPanel );
		this.panel.doLayout(); 
	},
	
	_onSelectionChange: function(dataGrid){
		
		var ids = [];
		// Get the selection
		dataGrid.getSelectedRecords().each(function(data){
			ids.push( data.data.id )
		})
		
		// Change the selection
		this.facade.changeSelection( ids );
	},
	
	_onDblClick: function(dataGrid, index, node, e){
		
		// Get the uri from the clicked model
		var id 	= dataGrid.getRecord( node ).data.id
		var uri = this.facade.modelCache.getModelUri( id )
		uri 	= uri.slice(1) + "/self";
		
		// Select the new range
		dataGrid.selectRange(index, index)
		
		// Open the model in a new window
		window.open( uri )
		
	}	
};

Repository.Plugins.IconView = Repository.Core.ViewPlugin.extend(Repository.Plugins.IconView);


DataGridPanel = Ext.extend(Ext.DataView, {
	multiSelect		: true,
	simpleSelect	: true, 
    cls				: 'repository_iconview',
    itemSelector	: 'dd',
    overClass		: 'over',
	selectedClass	: 'selected',
    tpl : new Ext.XTemplate(
        '<div>',
			'<dl>',
            '<tpl for=".">',
				'<dd>',
				'<img src="{icon}" title="{title}"/>',
	            '<div><span class="title">{title}</span><span class="author">({type})</span></div>',
	            '<div><span class="type">{author}</span></div>',
				'</dd>',
            '</tpl>',
			'</dl>',
        '</div>'
    )
});
