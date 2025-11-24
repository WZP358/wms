<template>
  <div class="warehouse-map-container">
    <div class="control-panel">
      <el-card class="warehouse-card">
        <template #header>
          <span>仓库选择</span>
        </template>
        <el-form label-width="80px">
          <el-form-item label="选择仓库">
            <el-select 
              v-model="selectedWarehouse" 
              placeholder="请选择仓库" 
              @change="onWarehouseChange"
              style="width: 100%"
            >
              <el-option
                v-for="warehouse in warehouseList"
                :key="warehouse.id"
                :label="warehouse.warehouseName"
                :value="warehouse.id"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="settings-card">
        <template #header>
          <span>地图设置</span>
        </template>
        <el-form :model="mapConfig" label-width="100px">
          <el-form-item label="地图宽度">
            <el-input-number v-model="mapConfig.cols" :min="3" :max="30" @change="initMap" />
            <span class="unit">列</span>
          </el-form-item>
          <el-form-item label="地图高度">
            <el-input-number v-model="mapConfig.rows" :min="3" :max="30" @change="initMap" />
            <span class="unit">行</span>
          </el-form-item>
          <el-form-item label="格子大小">
            <el-input-number v-model="mapConfig.cellSize" :min="40" :max="100" :step="10" />
            <span class="unit">px</span>
          </el-form-item>
          <el-form-item label="走廊宽度">
            <el-input-number v-model="mapConfig.gap" :min="10" :max="40" :step="5" />
            <span class="unit">px</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="initMap">重新生成地图</el-button>
            <el-button type="warning" @click="clearSelection">清空选择</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="areas-card">
        <template #header>
          <span>库区管理</span>
          <el-button 
            v-if="selectedWarehouse" 
            type="primary" 
            size="small" 
            style="float: right" 
            @click="showAddAreaDialog"
          >
            添加库区
          </el-button>
        </template>
        <div v-if="selectedWarehouse">
          <div class="areas-list">
            <div v-for="area in areaList" :key="area.id" class="area-item">
              <div class="area-color" :style="{ backgroundColor: area.color }"></div>
              <el-input 
                v-model="area.areaName" 
                size="small" 
                style="width: 100px" 
                @change="saveAreaList"
              />
              <el-input 
                v-model="area.areaCode" 
                size="small" 
                style="width: 80px" 
                placeholder="编号"
                @change="saveAreaList"
              />
              <el-button 
                type="danger" 
                size="small" 
                icon="Delete" 
                circle 
                @click="removeArea(area.id)"
              />
            </div>
          </div>
          <div class="area-hint">
            <el-alert title="右键点击格子设置库区" type="info" :closable="false" show-icon />
          </div>
        </div>
        <el-empty v-else description="请先选择仓库" :image-size="80" />
      </el-card>

      <el-card class="selection-card">
        <template #header>
          <span>选中的格子</span>
          <el-tag style="float: right" type="info">{{ selectedCells.length }} 个</el-tag>
        </template>
        <div class="selection-info">
          <el-alert 
            title="左键点击格子进行选择/取消" 
            type="info" 
            :closable="false"
            show-icon
          />
          <div class="selected-list" v-if="selectedCells.length > 0">
            <el-tag 
              v-for="cell in selectedCells" 
              :key="cell.index"
              closable
              @close="toggleCell(cell)"
              style="margin: 5px"
            >
              行{{ cell.row + 1 }}-列{{ cell.col + 1 }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <el-card class="path-card">
        <template #header>
          <span>路径规划</span>
        </template>
        <el-form label-width="120px">
          <el-form-item label="算法迭代次数">
            <el-input-number v-model="algorithmParams.iterations" :min="1000" :max="50000" :step="1000" />
          </el-form-item>
          <el-form-item label="初始温度">
            <el-input-number v-model="algorithmParams.temperature" :min="100" :max="10000" :step="100" />
          </el-form-item>
          <el-form-item label="冷却速率">
            <el-input-number v-model="algorithmParams.coolingRate" :min="0.90" :max="0.999" :step="0.001" :precision="3" />
          </el-form-item>
          <el-form-item>
            <el-button type="success" @click="calculatePath" :loading="calculating" :disabled="selectedCells.length === 0">
              计算最短路径
            </el-button>
            <el-button type="info" @click="clearPath">清除路径</el-button>
          </el-form-item>
          <el-form-item label="路径总距离" v-if="pathResult">
            <el-tag type="success" size="large">{{ pathResult.distance.toFixed(2) }} 像素</el-tag>
          </el-form-item>
          <el-form-item label="访问顺序" v-if="pathResult">
            <div class="visit-order">
              <el-tag v-for="(cell, index) in pathResult.visitOrder" :key="index" style="margin: 2px">
                {{ index + 1 }}. 行{{ cell.row + 1 }}-列{{ cell.col + 1 }}
              </el-tag>
            </div>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card class="animation-card" v-if="pathResult">
        <template #header>
          <span>路径动画</span>
        </template>
        <el-form label-width="100px">
          <el-form-item label="动画速度">
            <el-slider v-model="animationSpeed" :min="1" :max="10" :marks="{ 1: '很慢', 3: '慢', 5: '中', 7: '快', 10: '极速' }" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="playAnimation" :disabled="isAnimating">
              <el-icon><VideoPlay /></el-icon>
              播放动画
            </el-button>
            <el-button type="warning" @click="stopAnimation" :disabled="!isAnimating">
              <el-icon><VideoPause /></el-icon>
              停止
            </el-button>
            <el-button type="info" @click="resetAnimation">
              <el-icon><RefreshLeft /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <div class="map-container">
      <div 
        class="warehouse-map" 
        :style="{ 
          width: mapWidth + 'px',
          height: mapHeight + 'px'
        }"
      >
        <!-- Grid cells (shelves) -->
        <div
          v-for="(cell, index) in gridCells"
          :key="index"
          class="grid-cell"
          :class="{ 
            'is-door': cell.isDoor,
            'is-selected': cell.selected,
            'is-animating': isAnimating && currentAnimationCell === index,
            'has-area': cell.area && !cell.isDoor
          }"
          :style="getCellStyle(cell)"
          @click="toggleCell(cell)"
          @contextmenu="openContextMenu($event, cell)"
          @mouseenter="showCellInfo($event, cell)"
          @mouseleave="hideTooltip"
        >
          <span v-if="cell.isDoor" class="door-label">大门</span>
          <span v-if="cell.selected && !cell.isDoor" class="cell-label">
            行{{ cell.row + 1 }}-列{{ cell.col + 1 }}
          </span>
          <span v-if="cell.area && !cell.isDoor && !cell.selected" class="area-label">
            {{ getAreaName(cell.area) }}
          </span>
        </div>

        <!-- Context menu -->
        <div
          v-if="contextMenu.visible"
          class="context-menu"
          :style="{ left: contextMenu.x + 'px', top: contextMenu.y + 'px' }"
          @click.stop
        >
          <div class="context-menu-header">设置库区</div>
          <div
            v-for="area in areaList"
            :key="area.id"
            class="context-menu-item"
            @click="setCellArea(area.id)"
          >
            <div class="menu-color" :style="{ backgroundColor: area.color }"></div>
            <span>{{ area.areaName }}</span>
            <el-tag size="small" type="info">{{ area.areaCode }}</el-tag>
          </div>
          <div class="context-menu-divider"></div>
          <div class="context-menu-item" @click="setCellArea(null)">
            <el-icon><Delete /></el-icon>
            <span>清除库区</span>
          </div>
        </div>

        <!-- SVG for corridors and paths -->
        <svg class="path-layer" :width="mapWidth" :height="mapHeight">
          <!-- Draw corridors (gaps between cells) -->
          <g class="corridors">
            <!-- Horizontal corridors -->
            <rect
              v-for="(corridor, index) in horizontalCorridors"
              :key="'h-' + index"
              :x="corridor.x"
              :y="corridor.y"
              :width="corridor.width"
              :height="corridor.height"
              fill="#e0e0e0"
              stroke="#ccc"
              stroke-width="1"
            />
            <!-- Vertical corridors -->
            <rect
              v-for="(corridor, index) in verticalCorridors"
              :key="'v-' + index"
              :x="corridor.x"
              :y="corridor.y"
              :width="corridor.width"
              :height="corridor.height"
              fill="#e0e0e0"
              stroke="#ccc"
              stroke-width="1"
            />
          </g>

          <!-- Target points (bottom edge centers) -->
          <g v-if="selectedCells.length > 0">
            <circle
              v-for="(cell, index) in selectedCells"
              :key="'target-' + index"
              :cx="cell.targetX"
              :cy="cell.targetY"
              r="5"
              fill="#E6A23C"
              stroke="#fff"
              stroke-width="2"
            />
          </g>

          <!-- Static path lines -->
          <g v-if="pathResult && !isAnimating">
            <!-- Shadow/outline for better visibility -->
            <polyline
              :points="pathPolyline"
              fill="none"
              stroke="#000"
              stroke-width="6"
              stroke-linejoin="round"
              stroke-linecap="round"
              opacity="0.2"
            />
            <!-- Main path line -->
            <polyline
              :points="pathPolyline"
              fill="none"
              stroke="#409EFF"
              stroke-width="4"
              stroke-linejoin="round"
              stroke-linecap="round"
            />
            <!-- Path markers with order numbers (only at waypoints) -->
            <g v-for="(point, index) in pathResult.waypoints" :key="'marker-' + index">
              <circle
                :cx="point.x"
                :cy="point.y"
                :r="16"
                :fill="index === 0 ? '#67C23A' : index === pathResult.waypoints.length - 1 ? '#F56C6C' : '#409EFF'"
                opacity="0.95"
                stroke="#fff"
                stroke-width="2"
              />
              <text
                :x="point.x"
                :y="point.y + 5"
                text-anchor="middle"
                fill="white"
                font-size="14"
                font-weight="bold"
              >
                {{ index }}
              </text>
            </g>
          </g>
          
          <!-- Animated path -->
          <g v-if="isAnimating">
            <!-- Already walked path (darker) -->
            <polyline
              v-if="currentAnimationStep > 0"
              :points="animatedPolyline"
              fill="none"
              stroke="#67C23A"
              stroke-width="4"
              stroke-linejoin="round"
              stroke-linecap="round"
              opacity="0.8"
            />
            <!-- Remaining path (lighter) -->
            <polyline
              :points="pathPolyline"
              fill="none"
              stroke="#E0E0E0"
              stroke-width="3"
              stroke-linejoin="round"
              stroke-linecap="round"
              opacity="0.5"
            />
            <!-- Animated marker -->
            <g v-if="currentAnimationStep >= 0">
              <!-- Marker shadow -->
              <circle
                :cx="animatedPosition.x"
                :cy="animatedPosition.y"
                r="20"
                fill="#000"
                opacity="0.3"
              />
              <!-- Main marker -->
              <circle
                :cx="animatedPosition.x"
                :cy="animatedPosition.y"
                r="16"
                fill="#E6A23C"
                stroke="#fff"
                stroke-width="3"
                opacity="0.95"
              >
                <animate
                  attributeName="r"
                  values="14;18;14"
                  dur="0.6s"
                  repeatCount="indefinite"
                />
              </circle>
              <!-- Step number -->
              <text
                :x="animatedPosition.x"
                :y="animatedPosition.y + 5"
                text-anchor="middle"
                fill="white"
                font-size="14"
                font-weight="bold"
              >
                {{ Math.floor(currentAnimationStep / pathResult.path.length * pathResult.waypoints.length) }}
              </text>
            </g>
            <!-- Waypoint markers (static during animation) -->
            <g v-for="(point, index) in pathResult.waypoints" :key="'anim-marker-' + index">
              <circle
                :cx="point.x"
                :cy="point.y"
                :r="12"
                :fill="index === 0 ? '#67C23A' : index === pathResult.waypoints.length - 1 ? '#F56C6C' : '#909399'"
                opacity="0.5"
                stroke="#fff"
                stroke-width="2"
              />
              <text
                :x="point.x"
                :y="point.y + 4"
                text-anchor="middle"
                fill="white"
                font-size="11"
                font-weight="bold"
              >
                {{ index }}
              </text>
            </g>
          </g>
        </svg>

        <!-- Tooltip -->
        <div
          v-if="tooltip.visible"
          class="cell-tooltip"
          :style="{ left: tooltip.x + 'px', top: tooltip.y + 'px' }"
        >
          <div v-if="tooltip.cell.isDoor">
            <strong>仓库大门</strong>
            <p>起点/终点</p>
          </div>
          <div v-else>
            <strong>货架位置</strong>
            <p>行{{ tooltip.cell.row + 1 }}, 列{{ tooltip.cell.col + 1 }}</p>
            <p v-if="tooltip.cell.area">
              <span>库区: </span>
              <el-tag size="small" :color="areaList.find(a => a.id === tooltip.cell.area)?.color">
                {{ getAreaName(tooltip.cell.area) }}
              </el-tag>
            </p>
            <p v-if="tooltip.cell.selected" style="color: #67C23A">✓ 已选中用于路径规划</p>
            <p class="hint">左键: 选择/取消 | 右键: 设置库区</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { listWarehouses, listAreasByWarehouse, getWarehouseMap, saveWarehouseMap } from '@/api/wms/warehouseMap';

// Warehouse and Area data
const warehouseList = ref([]);
const selectedWarehouse = ref(null);
const areaList = ref([]);

// Map configuration
const mapConfig = ref({
  rows: 10,
  cols: 15,
  cellSize: 60,
  gap: 20  // 走廊宽度
});

// Grid cells data
const gridCells = ref([]);
const selectedCells = computed(() => gridCells.value.filter(cell => cell.selected && !cell.isDoor));

// Context menu
const contextMenu = ref({
  visible: false,
  x: 0,
  y: 0,
  cell: null
});

// Tooltip
const tooltip = ref({
  visible: false,
  x: 0,
  y: 0,
  cell: null
});

// Path calculation
const calculating = ref(false);
const pathResult = ref(null);

// Algorithm parameters
const algorithmParams = ref({
  iterations: 10000,
  temperature: 1000,
  coolingRate: 0.995
});

// Animation
const isAnimating = ref(false);
const currentAnimationStep = ref(-1);
const currentAnimationCell = ref(-1);
const animationSpeed = ref(5);
const animationTimer = ref(null);

// Computed properties
const mapWidth = computed(() => {
  const doorWidth = mapConfig.value.cellSize;
  const otherWidth = mapConfig.value.cols * mapConfig.value.cellSize + (mapConfig.value.cols + 1) * mapConfig.value.gap;
  return Math.max(doorWidth, otherWidth) + 40;
});

const mapHeight = computed(() => {
  return mapConfig.value.rows * mapConfig.value.cellSize + (mapConfig.value.rows + 1) * mapConfig.value.gap + 40;
});

// Corridor definitions
const horizontalCorridors = computed(() => {
  const corridors = [];
  const startX = (mapWidth.value - (mapConfig.value.cols * mapConfig.value.cellSize + (mapConfig.value.cols + 1) * mapConfig.value.gap)) / 2;
  
  for (let row = 0; row <= mapConfig.value.rows; row++) {
    const y = 20 + row * (mapConfig.value.cellSize + mapConfig.value.gap);
    corridors.push({
      x: startX,
      y: y,
      width: mapConfig.value.cols * mapConfig.value.cellSize + (mapConfig.value.cols + 1) * mapConfig.value.gap,
      height: mapConfig.value.gap
    });
  }
  
  return corridors;
});

const verticalCorridors = computed(() => {
  const corridors = [];
  const startX = (mapWidth.value - (mapConfig.value.cols * mapConfig.value.cellSize + (mapConfig.value.cols + 1) * mapConfig.value.gap)) / 2;
  
  for (let col = 0; col <= mapConfig.value.cols; col++) {
    const x = startX + col * (mapConfig.value.cellSize + mapConfig.value.gap);
    corridors.push({
      x: x,
      y: 20,
      width: mapConfig.value.gap,
      height: mapConfig.value.rows * mapConfig.value.cellSize + (mapConfig.value.rows + 1) * mapConfig.value.gap
    });
  }
  
  return corridors;
});

const pathPolyline = computed(() => {
  if (!pathResult.value || !pathResult.value.path) return '';
  return pathResult.value.path.map(p => `${p.x},${p.y}`).join(' ');
});

const animatedPolyline = computed(() => {
  if (!pathResult.value || !pathResult.value.path || currentAnimationStep.value < 0) return '';
  const stepIndex = Math.min(currentAnimationStep.value, pathResult.value.path.length - 1);
  const points = pathResult.value.path.slice(0, stepIndex + 1);
  return points.map(p => `${p.x},${p.y}`).join(' ');
});

const animatedPosition = computed(() => {
  if (!pathResult.value || !pathResult.value.path || currentAnimationStep.value < 0) {
    return { x: 0, y: 0 };
  }
  const stepIndex = Math.min(currentAnimationStep.value, pathResult.value.path.length - 1);
  return pathResult.value.path[stepIndex];
});

// Initialize map
const initMap = () => {
  stopAnimation();
  gridCells.value = [];
  
  // First row - only door
  const doorX = (mapWidth.value - mapConfig.value.cellSize) / 2;
  gridCells.value.push({
    index: 0,
    row: 0,
    col: 0,
    x: doorX,
    y: 20,
    isDoor: true,
    selected: false,
    targetX: doorX + mapConfig.value.cellSize / 2,
    targetY: 20 + mapConfig.value.cellSize  // 下底边中点
  });
  
  // Other rows
  const startX = (mapWidth.value - (mapConfig.value.cols * mapConfig.value.cellSize + (mapConfig.value.cols + 1) * mapConfig.value.gap)) / 2;
  
  let index = 1;
  for (let row = 1; row < mapConfig.value.rows; row++) {
    for (let col = 0; col < mapConfig.value.cols; col++) {
      const x = startX + mapConfig.value.gap + col * (mapConfig.value.cellSize + mapConfig.value.gap);
      const y = 20 + mapConfig.value.gap + row * (mapConfig.value.cellSize + mapConfig.value.gap);
      gridCells.value.push({
        index: index++,
        row,
        col,
        x,
        y,
        isDoor: false,
        selected: false,
        targetX: x + mapConfig.value.cellSize / 2,  // 下底边中点X
        targetY: y + mapConfig.value.cellSize        // 下底边中点Y
      });
    }
  }
  
  currentAnimationStep.value = -1;
  pathResult.value = null;
  ElMessage.success('地图已重新生成');
};

// Load warehouses
const loadWarehouses = async () => {
  try {
    const response = await listWarehouses();
    console.log('仓库列表响应:', response);
    
    if (response && response.data) {
      // 处理返回的仓库数据
      if (Array.isArray(response.data)) {
        warehouseList.value = response.data;
      } else if (response.data.rows && Array.isArray(response.data.rows)) {
        warehouseList.value = response.data.rows;
      } else {
        console.warn('仓库数据格式不正确:', response.data);
        warehouseList.value = [];
      }
      
      // 自动选择第一个仓库
      if (warehouseList.value.length > 0 && !selectedWarehouse.value) {
        selectedWarehouse.value = warehouseList.value[0].id;
        await onWarehouseChange();
      } else if (warehouseList.value.length === 0) {
        ElMessage.info('系统中暂无仓库，请先添加仓库');
      }
    }
  } catch (error) {
    console.error('加载仓库列表失败:', error);
    ElMessage.warning('加载仓库列表失败，请检查后端服务');
    // 可以提供模拟数据用于测试
    // warehouseList.value = [{ id: 1, warehouseName: '测试仓库' }];
  }
};

// Load areas by warehouse
const loadAreasByWarehouse = async (warehouseId) => {
  if (!warehouseId) {
    areaList.value = [];
    return;
  }
  
  // 首先尝试从本地存储加载
  const localAreas = loadAreasFromLocal(warehouseId);
  if (localAreas && localAreas.length > 0) {
    areaList.value = localAreas;
    console.log('从本地加载库区:', localAreas.length, '个');
    return;
  }
  
  // 如果本地没有，尝试从后端加载
  try {
    const response = await listAreasByWarehouse(warehouseId);
    console.log('库区列表响应:', response);
    
    if (response && response.data && Array.isArray(response.data) && response.data.length > 0) {
      // 处理返回的库区数据
      areaList.value = response.data.map(area => ({
        id: area.id,
        areaName: area.areaName,
        areaCode: area.areaCode,
        warehouseId: area.warehouseId,
        color: area.color || getRandomColor()
      }));
      
      // 保存到本地
      saveAreasToLocal(warehouseId, areaList.value);
      console.log('成功从后端加载库区:', areaList.value.length, '个');
    } else {
      // 如果后端没有数据，使用默认库区
      areaList.value = generateDefaultAreas(warehouseId);
      saveAreasToLocal(warehouseId, areaList.value);
      ElMessage.info('该仓库暂无库区，已创建默认库区，可以手动编辑');
    }
  } catch (error) {
    console.error('加载库区列表失败:', error);
    // 使用默认库区
    areaList.value = generateDefaultAreas(warehouseId);
    saveAreasToLocal(warehouseId, areaList.value);
    ElMessage.warning('无法连接后端服务，使用本地库区数据');
  }
};

// Load areas from localStorage
const loadAreasFromLocal = (warehouseId) => {
  try {
    const key = `warehouse_areas_${warehouseId}`;
    const saved = localStorage.getItem(key);
    if (saved) {
      return JSON.parse(saved);
    }
  } catch (error) {
    console.error('从本地加载库区失败:', error);
  }
  return null;
};

// Save areas to localStorage
const saveAreasToLocal = (warehouseId, areas) => {
  try {
    const key = `warehouse_areas_${warehouseId}`;
    localStorage.setItem(key, JSON.stringify(areas));
  } catch (error) {
    console.error('保存库区到本地失败:', error);
  }
};

// Save area list (triggered by edit)
const saveAreaList = () => {
  if (selectedWarehouse.value) {
    saveAreasToLocal(selectedWarehouse.value, areaList.value);
    ElMessage.success('库区信息已保存');
  }
};

// Generate default area data
const generateDefaultAreas = (warehouseId) => {
  return [
    { id: `area-${warehouseId}-${Date.now()}-1`, areaName: 'A区', areaCode: 'A001', warehouseId, color: '#409EFF' },
    { id: `area-${warehouseId}-${Date.now()}-2`, areaName: 'B区', areaCode: 'B001', warehouseId, color: '#67C23A' },
    { id: `area-${warehouseId}-${Date.now()}-3`, areaName: 'C区', areaCode: 'C001', warehouseId, color: '#E6A23C' },
    { id: `area-${warehouseId}-${Date.now()}-4`, areaName: 'D区', areaCode: 'D001', warehouseId, color: '#F56C6C' },
    { id: `area-${warehouseId}-${Date.now()}-5`, areaName: 'E区', areaCode: 'E001', warehouseId, color: '#909399' }
  ];
};

// Show add area dialog
const showAddAreaDialog = () => {
  if (!selectedWarehouse.value) {
    ElMessage.warning('请先选择仓库');
    return;
  }
  
  const areaNum = areaList.value.length + 1;
  const newArea = {
    id: `area-${selectedWarehouse.value}-${Date.now()}`,
    areaName: `${String.fromCharCode(64 + areaNum)}区`,
    areaCode: `${String.fromCharCode(64 + areaNum)}${String(areaNum).padStart(3, '0')}`,
    warehouseId: selectedWarehouse.value,
    color: getRandomColor()
  };
  
  areaList.value.push(newArea);
  saveAreasToLocal(selectedWarehouse.value, areaList.value);
  ElMessage.success('库区已添加');
};

// Remove area
const removeArea = (areaId) => {
  const index = areaList.value.findIndex(a => a.id === areaId);
  if (index > -1) {
    // 移除地图上该库区的设置
    gridCells.value.forEach(cell => {
      if (cell.area === areaId) {
        cell.area = null;
      }
    });
    
    // 从列表中移除
    areaList.value.splice(index, 1);
    saveAreasToLocal(selectedWarehouse.value, areaList.value);
    ElMessage.success('库区已删除');
  }
};

// Generate random color
const getRandomColor = () => {
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#C71585', '#FF8C00', '#00CED1'];
  return colors[Math.floor(Math.random() * colors.length)];
};

// On warehouse change
const onWarehouseChange = async () => {
  await loadAreasByWarehouse(selectedWarehouse.value);
  // Clear cell areas when warehouse changes
  gridCells.value.forEach(cell => {
    if (!cell.isDoor) {
      cell.area = null;
    }
  });
  clearPath();
  ElMessage.success('已切换仓库');
};

// Get cell style
const getCellStyle = (cell) => {
  const style = {
    left: cell.x + 'px',
    top: cell.y + 'px',
    width: mapConfig.value.cellSize + 'px',
    height: mapConfig.value.cellSize + 'px'
  };
  
  // Apply area color if cell has an area assigned
  if (cell.area) {
    const area = areaList.value.find(a => a.id === cell.area);
    if (area) {
      style.backgroundColor = area.color;
    }
  }
  
  return style;
};

// Toggle cell selection (left click)
const toggleCell = (cell) => {
  if (cell.isDoor) return;
  cell.selected = !cell.selected;
  clearPath();  // Clear path when selection changes
};

// Open context menu (right click)
const openContextMenu = (event, cell) => {
  if (cell.isDoor) return;
  if (!selectedWarehouse.value) {
    ElMessage.warning('请先选择仓库');
    return;
  }
  if (areaList.value.length === 0) {
    ElMessage.warning('该仓库没有库区');
    return;
  }
  
  event.preventDefault();
  event.stopPropagation();
  
  contextMenu.value = {
    visible: true,
    x: event.clientX - event.currentTarget.parentElement.getBoundingClientRect().left,
    y: event.clientY - event.currentTarget.parentElement.getBoundingClientRect().top,
    cell
  };
};

// Close context menu
const closeContextMenu = () => {
  contextMenu.value.visible = false;
};

// Set cell area
const setCellArea = (areaId) => {
  if (contextMenu.value.cell) {
    contextMenu.value.cell.area = areaId;
  }
  closeContextMenu();
};

// Clear selection
const clearSelection = () => {
  stopAnimation();
  gridCells.value.forEach(cell => {
    if (!cell.isDoor) {
      cell.selected = false;
    }
  });
  clearPath();
  ElMessage.success('已清空选择');
};

// Tooltip
const showCellInfo = (event, cell) => {
  const rect = event.currentTarget.getBoundingClientRect();
  const parentRect = event.currentTarget.parentElement.getBoundingClientRect();
  tooltip.value = {
    visible: true,
    x: rect.right - parentRect.left + 10,
    y: rect.top - parentRect.top,
    cell
  };
};

const hideTooltip = () => {
  tooltip.value.visible = false;
};

// Get area name
const getAreaName = (areaId) => {
  const area = areaList.value.find(a => a.id === areaId);
  return area ? area.areaName : '未知';
};

// Build corridor grid system
const buildCorridorGrid = () => {
  const grid = [];
  const startX = (mapWidth.value - (mapConfig.value.cols * mapConfig.value.cellSize + (mapConfig.value.cols + 1) * mapConfig.value.gap)) / 2;
  const startY = 20;
  
  // Create grid points at corridor intersections and midpoints
  const gridSpacing = 5; // 5px resolution
  
  for (let y = startY; y <= startY + mapConfig.value.rows * (mapConfig.value.cellSize + mapConfig.value.gap); y += gridSpacing) {
    for (let x = startX; x <= startX + mapConfig.value.cols * (mapConfig.value.cellSize + mapConfig.value.gap); x += gridSpacing) {
      // Check if this point is in a corridor (not inside a cell)
      if (isInCorridor(x, y, startX, startY)) {
        grid.push({ x, y });
      }
    }
  }
  
  return grid;
};

const isInCorridor = (x, y, startX, startY) => {
  // Check if point is in a corridor (not inside a cell)
  const relX = x - startX;
  const relY = y - startY;
  
  if (relX < 0 || relY < 0) return false;
  
  const cellWithGap = mapConfig.value.cellSize + mapConfig.value.gap;
  
  // Determine which cell column/row this point would be in
  const colIndex = Math.floor(relX / cellWithGap);
  const rowIndex = Math.floor(relY / cellWithGap);
  
  // Position within the cell+gap unit
  const posInCol = relX % cellWithGap;
  const posInRow = relY % cellWithGap;
  
  // Special handling for door row (row 0)
  if (rowIndex === 0) {
    // Only the main vertical corridor and horizontal corridors around door
    if (posInRow <= mapConfig.value.gap || posInRow >= mapConfig.value.cellSize + mapConfig.value.gap) {
      return true; // In horizontal corridor
    }
    // Check if in the central vertical corridor aligned with door
    const doorCell = gridCells.value.find(c => c.isDoor);
    if (doorCell && Math.abs(x - doorCell.targetX) <= mapConfig.value.gap / 2) {
      return true;
    }
    return false;
  }
  
  // For other rows: check if in gap area (corridor)
  const inVerticalCorridor = posInCol <= mapConfig.value.gap;
  const inHorizontalCorridor = posInRow <= mapConfig.value.gap;
  
  return inVerticalCorridor || inHorizontalCorridor;
};

// A* algorithm to find path in corridor grid
const findCorridorPath = (startPoint, endPoint) => {
  const gridSpacing = 5;
  
  // Snap points to grid
  const start = {
    x: Math.round(startPoint.x / gridSpacing) * gridSpacing,
    y: Math.round(startPoint.y / gridSpacing) * gridSpacing
  };
  const end = {
    x: Math.round(endPoint.x / gridSpacing) * gridSpacing,
    y: Math.round(endPoint.y / gridSpacing) * gridSpacing
  };
  
  const openSet = [start];
  const cameFrom = new Map();
  const gScore = new Map();
  const fScore = new Map();
  
  const key = (p) => `${p.x},${p.y}`;
  
  gScore.set(key(start), 0);
  fScore.set(key(start), manhattanDistance(start, end));
  
  const startX = (mapWidth.value - (mapConfig.value.cols * mapConfig.value.cellSize + (mapConfig.value.cols + 1) * mapConfig.value.gap)) / 2;
  const startY = 20;
  
  let iterations = 0;
  const maxIterations = 10000;
  
  while (openSet.length > 0 && iterations < maxIterations) {
    iterations++;
    
    // Find node with lowest fScore
    let current = openSet[0];
    let currentIdx = 0;
    for (let i = 1; i < openSet.length; i++) {
      if (fScore.get(key(openSet[i])) < fScore.get(key(current))) {
        current = openSet[i];
        currentIdx = i;
      }
    }
    
    // Reached goal
    if (Math.abs(current.x - end.x) <= gridSpacing && Math.abs(current.y - end.y) <= gridSpacing) {
      return reconstructPath(cameFrom, current);
    }
    
    openSet.splice(currentIdx, 1);
    
    // Check neighbors (4-directional movement)
    const neighbors = [
      { x: current.x + gridSpacing, y: current.y },
      { x: current.x - gridSpacing, y: current.y },
      { x: current.x, y: current.y + gridSpacing },
      { x: current.x, y: current.y - gridSpacing }
    ];
    
    for (const neighbor of neighbors) {
      // Check if neighbor is in corridor
      if (!isInCorridor(neighbor.x, neighbor.y, startX, startY)) {
        continue;
      }
      
      const tentativeGScore = gScore.get(key(current)) + gridSpacing;
      
      if (!gScore.has(key(neighbor)) || tentativeGScore < gScore.get(key(neighbor))) {
        cameFrom.set(key(neighbor), current);
        gScore.set(key(neighbor), tentativeGScore);
        fScore.set(key(neighbor), tentativeGScore + manhattanDistance(neighbor, end));
        
        if (!openSet.some(p => key(p) === key(neighbor))) {
          openSet.push(neighbor);
        }
      }
    }
  }
  
  // If no path found, return direct connection (fallback)
  return [startPoint, endPoint];
};

const reconstructPath = (cameFrom, current) => {
  const path = [current];
  const key = (p) => `${p.x},${p.y}`;
  
  while (cameFrom.has(key(current))) {
    current = cameFrom.get(key(current));
    path.unshift(current);
  }
  
  return path;
};

const manhattanDistance = (p1, p2) => {
  return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
};

// Calculate path using Simulated Annealing for visit order, then A* for actual paths
const calculatePath = () => {
  if (selectedCells.value.length === 0) {
    ElMessage.warning('请至少选择一个格子');
    return;
  }
  
  calculating.value = true;
  
  // Get door position
  const doorCell = gridCells.value.find(c => c.isDoor);
  const startPoint = {
    x: doorCell.targetX,
    y: doorCell.targetY - mapConfig.value.cellSize  // 从大门下方出发
  };
  
  // Get target points (bottom edge centers)
  const targetPoints = selectedCells.value.map(cell => ({
    x: cell.targetX,
    y: cell.targetY,
    cell: cell
  }));
  
  // Run simulated annealing to find best visit order
  setTimeout(() => {
    const visitOrder = findBestVisitOrder([startPoint, ...targetPoints, startPoint]);
    
    // Now find actual corridor paths between each pair of points
    const fullPath = [];
    const waypoints = []; // Key waypoints for markers
    let totalDistance = 0;
    
    waypoints.push(visitOrder[0]); // Start point
    
    for (let i = 0; i < visitOrder.length - 1; i++) {
      const segmentPath = findCorridorPath(visitOrder[i], visitOrder[i + 1]);
      
      // Simplify segment path to reduce points
      const simplifiedSegment = simplifyPath(segmentPath, 10);
      
      // Add segment to full path (avoid duplicating points)
      if (i === 0) {
        fullPath.push(...simplifiedSegment);
      } else {
        fullPath.push(...simplifiedSegment.slice(1));
      }
      
      // Add waypoint
      if (i < visitOrder.length - 2) {
        waypoints.push(visitOrder[i + 1]);
      }
      
      // Calculate segment distance
      for (let j = 0; j < segmentPath.length - 1; j++) {
        totalDistance += manhattanDistance(segmentPath[j], segmentPath[j + 1]);
      }
    }
    
    waypoints.push(visitOrder[visitOrder.length - 1]); // End point
    
    console.log('路径计算完成:', {
      总路径点数: fullPath.length,
      关键路径点数: waypoints.length,
      总距离: totalDistance
    });
    
    pathResult.value = {
      path: fullPath,
      waypoints: waypoints, // For markers
      distance: totalDistance,
      visitOrder: visitOrder.slice(1, -1).map(p => p.cell).filter(c => c)
    };
    
    calculating.value = false;
    ElMessage.success(`路径计算完成！总距离: ${totalDistance.toFixed(2)} 像素，${fullPath.length}个路径点`);
  }, 100);
};

// Simplify path by removing collinear points
const simplifyPath = (path, tolerance = 5) => {
  if (path.length <= 2) return path;
  
  const simplified = [path[0]];
  
  for (let i = 1; i < path.length - 1; i++) {
    const prev = simplified[simplified.length - 1];
    const curr = path[i];
    const next = path[i + 1];
    
    // Check if current point is on the same line as prev and next
    const dx1 = curr.x - prev.x;
    const dy1 = curr.y - prev.y;
    const dx2 = next.x - curr.x;
    const dy2 = next.y - curr.y;
    
    // If direction changes or distance is significant, keep the point
    const directionChange = (dx1 !== 0 && dx2 === 0) || (dx1 === 0 && dx2 !== 0) ||
                           (dy1 !== 0 && dy2 === 0) || (dy1 === 0 && dy2 !== 0);
    
    if (directionChange || i % 20 === 0) { // Keep turning points and some intermediate points
      simplified.push(curr);
    }
  }
  
  simplified.push(path[path.length - 1]);
  return simplified;
};

// Find best visit order using Simulated Annealing
const findBestVisitOrder = (points) => {
  const mapSize = mapConfig.value.rows * mapConfig.value.cols;
  const iterations = Math.max(algorithmParams.value.iterations, mapSize * 100);
  let temperature = algorithmParams.value.temperature;
  const coolingRate = algorithmParams.value.coolingRate;
  
  // Initial solution
  let currentSolution = [...points];
  const start = currentSolution[0];
  const end = currentSolution[currentSolution.length - 1];
  let middle = currentSolution.slice(1, -1);
  middle = shuffleArray(middle);
  currentSolution = [start, ...middle, end];
  
  let currentDistance = estimatePathDistance(currentSolution);
  let bestSolution = [...currentSolution];
  let bestDistance = currentDistance;
  
  // Annealing process
  for (let i = 0; i < iterations; i++) {
    const newSolution = generateNeighbor(currentSolution);
    const newDistance = estimatePathDistance(newSolution);
    
    const delta = newDistance - currentDistance;
    const acceptanceProbability = delta < 0 ? 1 : Math.exp(-delta / temperature);
    
    if (Math.random() < acceptanceProbability) {
      currentSolution = newSolution;
      currentDistance = newDistance;
      
      if (currentDistance < bestDistance) {
        bestSolution = [...currentSolution];
        bestDistance = currentDistance;
      }
    }
    
    temperature *= coolingRate;
  }
  
  return bestSolution;
};

// Estimate path distance (Manhattan distance as heuristic)
const estimatePathDistance = (path) => {
  let total = 0;
  for (let i = 0; i < path.length - 1; i++) {
    total += manhattanDistance(path[i], path[i + 1]);
  }
  return total;
};

// Generate neighbor solution
const generateNeighbor = (solution) => {
  const newSolution = [...solution];
  const n = solution.length;
  
  if (n <= 3) return newSolution;
  
  const i = Math.floor(Math.random() * (n - 2)) + 1;
  let j = Math.floor(Math.random() * (n - 2)) + 1;
  while (i === j) {
    j = Math.floor(Math.random() * (n - 2)) + 1;
  }
  
  [newSolution[i], newSolution[j]] = [newSolution[j], newSolution[i]];
  
  return newSolution;
};

// Shuffle array
const shuffleArray = (array) => {
  const result = [...array];
  for (let i = result.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [result[i], result[j]] = [result[j], result[i]];
  }
  return result;
};

// Clear path
const clearPath = () => {
  stopAnimation();
  currentAnimationStep.value = -1;
  pathResult.value = null;
  ElMessage.info('已清除路径');
};

// Animation functions
const playAnimation = () => {
  if (!pathResult.value || !pathResult.value.path || pathResult.value.path.length === 0) {
    ElMessage.warning('没有可播放的路径');
    return;
  }
  
  if (isAnimating.value) {
    ElMessage.warning('动画正在播放中');
    return;
  }
  
  // Reset and start animation
  isAnimating.value = true;
  currentAnimationStep.value = 0;
  
  console.log('开始播放动画，路径点数:', pathResult.value.path.length);
  
  // Adjust speed based on user setting
  // Speed 1 (slowest) = 300ms per step (very slow, easy to observe)
  // Speed 5 (medium) = 100ms per step (good balance)
  // Speed 10 (fastest) = 20ms per step (quick preview)
  const speedMap = {
    1: 300,  // 非常慢
    2: 250,  // 很慢
    3: 200,  // 慢
    4: 150,  // 较慢
    5: 100,  // 中速
    6: 80,   // 较快
    7: 60,   // 快
    8: 40,   // 很快
    9: 30,   // 非常快
    10: 20   // 极速
  };
  const baseSpeed = speedMap[animationSpeed.value] || 100;
  
  animationTimer.value = setInterval(() => {
    if (currentAnimationStep.value < pathResult.value.path.length - 1) {
      currentAnimationStep.value++;
    } else {
      // Animation complete
      stopAnimation();
      ElMessage.success('动画播放完成');
    }
  }, baseSpeed);
  
  ElMessage.success('开始播放动画');
};

const stopAnimation = () => {
  if (animationTimer.value) {
    clearInterval(animationTimer.value);
    animationTimer.value = null;
  }
  isAnimating.value = false;
  console.log('停止动画，当前步骤:', currentAnimationStep.value);
};

const resetAnimation = () => {
  stopAnimation();
  currentAnimationStep.value = -1;
  currentAnimationCell.value = -1;
  ElMessage.info('动画已重置');
};

// Auto-save to localStorage
const saveToLocalStorage = () => {
  const data = {
    mapConfig: mapConfig.value,
    selectedIndices: selectedCells.value.map(c => c.index)
  };
  localStorage.setItem('warehouseMapData', JSON.stringify(data));
};

const loadFromLocalStorage = () => {
  try {
    const saved = localStorage.getItem('warehouseMapData');
    if (saved) {
      const data = JSON.parse(saved);
      if (data.mapConfig) {
        mapConfig.value = data.mapConfig;
        initMap();
        
        if (data.selectedIndices) {
          data.selectedIndices.forEach(index => {
            const cell = gridCells.value.find(c => c.index === index);
            if (cell) cell.selected = true;
          });
        }
        
        ElMessage.success('已加载上次保存的配置');
      }
    }
  } catch (error) {
    console.error('加载配置失败:', error);
  }
};

// Click outside to close context menu
const handleClickOutside = (event) => {
  if (contextMenu.value.visible) {
    closeContextMenu();
  }
};

// Watch for changes
watch([gridCells, mapConfig], () => {
  saveToLocalStorage();
}, { deep: true });

// Lifecycle hooks
onMounted(async () => {
  // Load warehouses
  await loadWarehouses();
  
  // Load saved data
  const saved = localStorage.getItem('warehouseMapData');
  if (saved) {
    loadFromLocalStorage();
  } else {
    initMap();
  }
  
  // Add click listener
  document.addEventListener('click', handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<style scoped lang="scss">
.warehouse-map-container {
  display: flex;
  gap: 20px;
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.control-panel {
  width: 350px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.warehouse-card,
.settings-card,
.areas-card,
.selection-card,
.path-card,
.animation-card {
  .unit {
    margin-left: 10px;
    color: #909399;
  }
}

.areas-list {
  max-height: 300px;
  overflow-y: auto;
  margin-bottom: 10px;
  
  .area-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
    margin-bottom: 8px;
    background-color: #f5f7fa;
    border-radius: 4px;
    transition: all 0.2s;
    
    &:hover {
      background-color: #e8eaf0;
    }
    
    .area-color {
      width: 25px;
      height: 25px;
      border-radius: 4px;
      border: 2px solid #dcdfe6;
      flex-shrink: 0;
      cursor: pointer;
    }
  }
}

.area-hint {
  margin-top: 10px;
}

.selection-info {
  .selected-list {
    margin-top: 15px;
    max-height: 200px;
    overflow-y: auto;
  }
}

.visit-order {
  max-height: 150px;
  overflow-y: auto;
}

.map-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.warehouse-map {
  position: relative;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.path-layer {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
  z-index: 1;
}

.grid-cell {
  position: absolute;
  border: 2px solid #606266;
  border-radius: 4px;
  background-color: #dcdfe6;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  color: #303133;
  user-select: none;
  z-index: 2;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    z-index: 3;
  }
  
  &.is-door {
    background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
    border-color: #67C23A;
    font-size: 16px;
    color: white;
    cursor: default;
    
    &:hover {
      transform: none;
    }
  }
  
  &.is-selected {
    background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
    border-color: #409EFF;
    color: white;
  }
  
  &.has-area {
    color: white;
    font-weight: 500;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  }
  
  &.is-animating {
    animation: pulse 0.5s infinite;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1.05);
  }
  50% {
    transform: scale(1.15);
  }
}

.door-label,
.cell-label,
.area-label {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  font-size: 12px;
}

.context-menu {
  position: absolute;
  background-color: white;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  padding: 6px 0;
  z-index: 2000;
  min-width: 180px;
  max-height: 400px;
  overflow-y: auto;
  
  .context-menu-header {
    padding: 8px 16px;
    font-size: 13px;
    color: #909399;
    font-weight: 500;
    border-bottom: 1px solid #ebeef5;
    margin-bottom: 6px;
  }
  
  .context-menu-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 8px 16px;
    cursor: pointer;
    transition: background-color 0.2s;
    font-size: 14px;
    
    &:hover {
      background-color: #f5f7fa;
    }
    
    .menu-color {
      width: 20px;
      height: 20px;
      border-radius: 3px;
      border: 1px solid #dcdfe6;
      flex-shrink: 0;
    }
    
    span {
      flex: 1;
    }
  }
  
  .context-menu-divider {
    height: 1px;
    background-color: #ebeef5;
    margin: 6px 0;
  }
}

.cell-tooltip {
  position: absolute;
  background-color: rgba(0, 0, 0, 0.85);
  color: white;
  padding: 10px 15px;
  border-radius: 4px;
  font-size: 13px;
  z-index: 1001;
  pointer-events: none;
  white-space: nowrap;
  
  strong {
    display: block;
    margin-bottom: 5px;
    font-size: 14px;
  }
  
  p {
    margin: 3px 0;
    
    &.hint {
      color: #67C23A;
      font-style: italic;
    }
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
  
  &:hover {
    background: #555;
  }
}
</style>
