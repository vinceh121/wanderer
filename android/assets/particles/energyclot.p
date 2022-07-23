{
	"unique": {
		"billboardBatch": {
			"class": "com.badlogic.gdx.graphics.g3d.particles.ResourceData$SaveData",
			"data": {
				"cfg": {
					"class": "com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch$Config",
					"mode": "Screen"
				}
			},
			"indices": [
				0
			]
		}
	},
	"data": [
	],
	"assets": [
		{
			"filename": "orig/lib/glow11/glownone.ktx",
			"type": "com.badlogic.gdx.graphics.Texture"
		}
	],
	"resource": {
		"class": "com.badlogic.gdx.graphics.g3d.particles.ParticleEffect",
		"controllers": [
			{
				"name": "Billboard Controller",
				"emitter": {
					"class": "com.badlogic.gdx.graphics.g3d.particles.emitters.RegularEmitter",
					"minParticleCount": 0,
					"maxParticleCount": 200,
					"continous": true,
					"emission": {
						"active": true,
						"lowMin": 0,
						"lowMax": 0,
						"highMin": 250,
						"highMax": 250,
						"relative": false,
						"scaling": [
							1
						],
						"timeline": [
							0
						]
					},
					"delay": {
						"active": false,
						"lowMin": 0,
						"lowMax": 0
					},
					"duration": {
						"active": true,
						"lowMin": 3000,
						"lowMax": 3000
					},
					"life": {
						"active": true,
						"lowMin": 0,
						"lowMax": 0,
						"highMin": 500,
						"highMax": 1000,
						"relative": false,
						"scaling": [
							1,
							1,
							0.3
						],
						"timeline": [
							0,
							0.66,
							1
						]
					},
					"lifeOffset": {
						"active": false,
						"lowMin": 0,
						"lowMax": 0,
						"highMin": 0,
						"highMax": 0,
						"relative": false,
						"scaling": [
							1
						],
						"timeline": [
							0
						]
					}
				},
				"influencers": [
					{
						"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer$Single",
						"regions": [
							{
								"u2": 1,
								"v2": 1,
								"halfInvAspectRatio": 0.5
							}
						]
					},
					{
						"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.SpawnInfluencer",
						"spawnShape": {
							"class": "com.badlogic.gdx.graphics.g3d.particles.values.PointSpawnShapeValue",
							"active": false,
							"xOffsetValue": {
								"active": true,
								"lowMin": 0,
								"lowMax": 0.5
							},
							"yOffsetValue": {
								"active": true,
								"lowMin": 0,
								"lowMax": 0.5
							},
							"zOffsetValue": {
								"active": true,
								"lowMin": 0,
								"lowMax": 0.5
							},
							"spawnWidthValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 0,
								"highMax": 0,
								"relative": false,
								"scaling": [
									1
								],
								"timeline": [
									0
								]
							},
							"spawnHeightValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 0,
								"highMax": 0,
								"relative": false,
								"scaling": [
									1
								],
								"timeline": [
									0
								]
							},
							"spawnDepthValue": {
								"active": false,
								"lowMin": 0,
								"lowMax": 0,
								"highMin": 0,
								"highMax": 0,
								"relative": false,
								"scaling": [
									1
								],
								"timeline": [
									0
								]
							},
							"edges": false
						}
					},
					{
						"class": "com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer$Single",
						"alpha": {
							"active": false,
							"lowMin": 0,
							"lowMax": 0,
							"highMin": 1,
							"highMax": 1,
							"relative": false,
							"scaling": [
								1
							],
							"timeline": [
								0
							]
						},
						"color": {
							"active": false,
							"colors": [
								0,
								0.25490198,
								1
							],
							"timeline": [
								0
							]
						}
					}
				],
				"renderer": {
					"class": "com.badlogic.gdx.graphics.g3d.particles.renderers.BillboardRenderer"
				}
			}
		]
	}
}
